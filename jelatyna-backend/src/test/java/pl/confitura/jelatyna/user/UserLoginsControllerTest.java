package pl.confitura.jelatyna.user;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.ResultActions;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.infrastructure.security.TokenService;
import pl.confitura.jelatyna.login.OAuthUserService;
import pl.confitura.jelatyna.login.github.GitHubUser;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;
import pl.confitura.jelatyna.registration.voucher.VoucherService;

class UserLoginsControllerTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private OAuthUserService oauthUserService;

    @Autowired
    private PresentationRepository presentationRepository;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private ParticipationRepository participationRepository;

    @Value("${app.jwt.secret-key}")
    private String jwtSecretKey;

    @Test
    void listsTheUsersLoginsWithProviderAndDateAdded() throws Exception {
        User user = userRepository.save(new User().setName("Signed in twice"));
        loginRepository.save(new Login(user, "google", uniqueId()));
        loginRepository.save(new Login(user, "github", uniqueId()));

        mockMvc.perform(get("/users/" + user.getId() + "/logins")
                        .header(AUTHORIZATION, bearer(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].provider").value(containsInAnyOrder("google", "github")))
                .andExpect(jsonPath("$[0].createdDate").isNotEmpty());
    }

    @Test
    void anotherUsersLoginsAreForbidden() throws Exception {
        User owner = userRepository.save(new User().setName("Owner"));
        User someoneElse = userRepository.save(new User().setName("Someone else"));

        mockMvc.perform(get("/users/" + owner.getId() + "/logins")
                        .header(AUTHORIZATION, bearer(someoneElse)))
                .andExpect(status().isForbidden());
    }

    @Test
    void linkingANeverSeenLoginMovesItToTheSignedInUserAndDeletesTheUserItCreated() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in").setPrivacyPolicyAccepted(true));
        String githubId = uniqueId();
        // The second sign-in, with a Login never seen before, creates a brand-new User.
        String secondToken = tokenService.asToken(oauthUserService.mapToUser(gitHubUser(githubId)));

        String survivorToken = merge(signedIn, secondToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(tokenService.toUser(survivorToken).getId()).isEqualTo(signedIn.getId());
        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isEqualTo(signedIn.getId());
        mockMvc.perform(post("/tokens/refresh-token").header(AUTHORIZATION, "Bearer " + secondToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aSecondTokenIssuedMinutesAgoIsRejected() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        String githubId = uniqueId();
        User brandNew = oauthUserService.mapToUser(gitHubUser(githubId));

        merge(signedIn, tokenSignedInAt(brandNew, Instant.now().minus(Duration.ofMinutes(10))))
                .andExpect(status().isForbidden());

        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isEqualTo(brandNew.getId());
    }

    @Test
    void aRefreshedOldTokenIsNotProofOfAFreshSignIn() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        User brandNew = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        // A leaked token: refreshing it issues a new one, but nobody has signed in again.
        String refreshed = refresh(tokenSignedInAt(brandNew, Instant.now().minus(Duration.ofMinutes(10))));

        merge(signedIn, refreshed).andExpect(status().isForbidden());
        cancelMerge(signedIn, refreshed).andExpect(status().isForbidden());

        assertThat(userRepository.existsById(brandNew.getId())).isTrue();
    }

    @Test
    void aTokenThatDoesNotSayWhenThePersonSignedInIsNeverProof() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        User brandNew = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        // Issued just now, but without auth_time, as tokens were before it was recorded.
        String legacy = Jwts.builder().claim("isAdmin", false).claim("isVolunteer", false)
                .id(brandNew.getId()).issuedAt(new Date()).signWith(serverKey()).compact();

        merge(signedIn, legacy).andExpect(status().isForbidden());
        merge(signedIn, refresh(legacy)).andExpect(status().isForbidden());
    }

    @Test
    void theTokenMergeReturnsKeepsWhenTheSurvivorSignedIn() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        Instant anHourAgo = Instant.now().minus(Duration.ofHours(1)).truncatedTo(SECONDS);
        String secondToken = tokenService.asToken(oauthUserService.mapToUser(gitHubUser(uniqueId())));

        String survivorToken = merge(tokenSignedInAt(signedIn, anHourAgo), secondToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(Jwts.parser().verifyWith(serverKey()).build().parseSignedClaims(survivorToken).getPayload()
                .get("auth_time", Long.class)).isEqualTo(anHourAgo.getEpochSecond());
    }

    @Test
    void mergingNeedsASignedInUser() throws Exception {
        String secondToken = tokenService.asToken(oauthUserService.mapToUser(gitHubUser(uniqueId())));

        mockMvc.perform(post("/users/merge")
                        .contentType(APPLICATION_JSON)
                        .content(json(new UserLoginsController.MergeUsersRequest(secondToken))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aSecondTokenNotSignedByUsIsRejected() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        User brandNew = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        String forged = Jwts.builder().id(brandNew.getId()).issuedAt(new Date())
                .claim("auth_time", Instant.now().getEpochSecond()).signWith(Jwts.SIG.HS256.key().build()).compact();

        merge(signedIn, forged)
                .andExpect(status().isForbidden());
    }

    @Test
    void signingInAgainAsTheSignedInUserMergesNothing() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));

        merge(signedIn, tokenService.asToken(signedIn))
                .andExpect(status().isConflict());

        assertThat(userRepository.existsById(signedIn.getId())).isTrue();
    }

    enum Content { SAVED_PROFILE, TALK, ADMIN_ROLE, VOLUNTEER_ROLE, TICKET }

    // Merging two Users that both hold something comes later; for now only an empty one is absorbed.
    @ParameterizedTest
    @EnumSource
    void aUserWithContentIsNotMergedYet(Content content) throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        String githubId = uniqueId();
        User other = give(content, oauthUserService.mapToUser(gitHubUser(githubId)));

        merge(signedIn, tokenService.asToken(other))
                .andExpect(status().isConflict());

        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isEqualTo(other.getId());
    }

    private User give(Content content, User user) {
        switch (content) {
            case SAVED_PROFILE -> userRepository.save(user.setPrivacyPolicyAccepted(true));
            case TALK -> presentationRepository.save(Presentation.from(
                    new PresentationRequest("Talk", "short", "long", "beginner", "Polish", new String[]{}), user, Set.of()));
            case ADMIN_ROLE -> userRepository.save(user.setAdmin(true));
            case VOLUNTEER_ROLE -> userRepository.save(user.setVolunteer(true));
            case TICKET -> {
                // Only an admin issues vouchers. No arrival date: other tests count who arrived.
                SecurityHelper.asAdmin();
                ParticipationData ticket = participationRepository.save(new ParticipationData().setVoucher(voucherService.generateVoucher("")));
                SecurityHelper.cleanSecurity();
                userRepository.save(user.setParticipationData(ticket));
            }
        }
        return user;
    }

    @Test
    void aSecondSignInThatWasAlreadyMergedIsNotFound() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        String secondToken = tokenService.asToken(oauthUserService.mapToUser(gitHubUser(uniqueId())));
        merge(signedIn, secondToken).andExpect(status().isOk());

        merge(signedIn, secondToken).andExpect(status().isNotFound());
    }

    @Test
    void eachLinkLogsOneLineWithIdsOnly() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in").setEmail("signed.in@example.com"));
        String githubId = uniqueId();
        User brandNew = oauthUserService.mapToUser(gitHubUser(githubId));
        Login login = loginRepository.findByProviderAndExternalId("github", githubId).orElseThrow();
        Logger logger = (Logger) LoggerFactory.getLogger(UserLoginsController.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            merge(signedIn, tokenService.asToken(brandNew)).andExpect(status().isOk());
        } finally {
            logger.detachAppender(appender);
        }

        assertThat(appender.list).singleElement().satisfies(event -> {
            assertThat(event.getKeyValuePairs())
                    .extracting(pair -> pair.key, pair -> pair.value)
                    .containsExactlyInAnyOrder(
                            tuple("actorUserId", signedIn.getId()),
                            tuple("survivorUserId", signedIn.getId()),
                            tuple("absorbedUserId", brandNew.getId()),
                            tuple("provider", "github"),
                            tuple("loginId", login.getId()));
            assertThat(event.getFormattedMessage()).doesNotContain("Signed in", "signed.in@example.com", "Octo Cat", githubId);
        });
    }

    @Test
    void cancellingDeletesTheUserTheSecondSignInCreated() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        String githubId = uniqueId();
        User brandNew = oauthUserService.mapToUser(gitHubUser(githubId));
        String secondToken = tokenService.asToken(brandNew);

        cancelMerge(signedIn, secondToken).andExpect(status().isNoContent());

        mockMvc.perform(post("/tokens/refresh-token").header(AUTHORIZATION, "Bearer " + secondToken))
                .andExpect(status().isUnauthorized());
        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isNotEqualTo(brandNew.getId());
        assertThat(userRepository.existsById(signedIn.getId())).isTrue();
    }

    @Test
    void cancellingLeavesAUserWithContentAlone() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        String githubId = uniqueId();
        User other = give(Content.SAVED_PROFILE, oauthUserService.mapToUser(gitHubUser(githubId)));

        cancelMerge(signedIn, tokenService.asToken(other)).andExpect(status().isConflict());

        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isEqualTo(other.getId());
    }

    @Test
    void cancellingWithTheSignedInUsersOwnTokenDeletesNothing() throws Exception {
        // Not yet saved a profile, so as empty as a brand-new User.
        User signedIn = userRepository.save(new User().setName("Signed in"));

        cancelMerge(signedIn, tokenService.asToken(signedIn)).andExpect(status().isConflict());

        assertThat(userRepository.existsById(signedIn.getId())).isTrue();
    }

    @Test
    void cancellingWithAStaleSecondTokenDeletesNothing() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        User brandNew = oauthUserService.mapToUser(gitHubUser(uniqueId()));

        cancelMerge(signedIn, tokenSignedInAt(brandNew, Instant.now().minus(Duration.ofMinutes(10)))).andExpect(status().isForbidden());

        assertThat(userRepository.existsById(brandNew.getId())).isTrue();
    }

    private ResultActions cancelMerge(User signedIn, String secondToken) throws Exception {
        return mockMvc.perform(post("/users/merge/cancel")
                .header(AUTHORIZATION, bearer(signedIn))
                .contentType(APPLICATION_JSON)
                .content(json(new UserLoginsController.MergeUsersRequest(secondToken))));
    }

    private ResultActions merge(User signedIn, String secondToken) throws Exception {
        return merge(tokenService.asToken(signedIn), secondToken);
    }

    private ResultActions merge(String signedInToken, String secondToken) throws Exception {
        return mockMvc.perform(post("/users/merge")
                .header(AUTHORIZATION, "Bearer " + signedInToken)
                .contentType(APPLICATION_JSON)
                .content(json(new UserLoginsController.MergeUsersRequest(secondToken))));
    }

    private String bearer(User user) {
        return "Bearer " + tokenService.asToken(user);
    }

    private String refresh(String token) throws Exception {
        return mockMvc.perform(post("/tokens/refresh-token").header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    // Signed with the server's key, as an old or leaked token would be.
    private String tokenSignedInAt(User user, Instant signedInAt) {
        return Jwts.builder()
                .claim("isAdmin", false)
                .claim("isVolunteer", false)
                .claim("auth_time", signedInAt.getEpochSecond())
                .id(user.getId())
                .issuedAt(Date.from(signedInAt))
                .signWith(serverKey())
                .compact();
    }

    private SecretKey serverKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    private static GitHubUser gitHubUser(String id) {
        GitHubUser user = new GitHubUser();
        user.setId(id);
        user.setName("Octo Cat");
        return user;
    }

    private static String uniqueId() {
        return UUID.randomUUID().toString();
    }
}
