package pl.confitura.jelatyna.identity.user.web;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.ResultActions;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRequest;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.platform.security.JelatynaPrincipal;
import pl.confitura.jelatyna.identity.user.domain.Login;
import pl.confitura.jelatyna.identity.user.domain.LoginRepository;
import pl.confitura.jelatyna.identity.user.domain.TokenService;
import pl.confitura.jelatyna.identity.user.domain.User;
import pl.confitura.jelatyna.identity.user.domain.UserRepository;
import pl.confitura.jelatyna.identity.login.domain.OAuthUserService;
import pl.confitura.jelatyna.identity.login.github.GitHubUser;
import pl.confitura.jelatyna.conference.presentation.domain.Presentation;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationData;
import pl.confitura.jelatyna.conference.registration.domain.ParticipationRepository;
import pl.confitura.jelatyna.conference.registration.domain.VoucherService;

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
    private EditionRepository editionRepository;

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
        // The preview shows the other User's profile, so it needs the same proof.
        previewMerge(signedIn, refreshed).andExpect(status().isForbidden());
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

    @ParameterizedTest
    @EnumSource
    void aUserWithAProfileARoleTalksOrATicketIsMergedAndDeleted(Content content) throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        String githubId = uniqueId();
        User other = give(content, oauthUserService.mapToUser(gitHubUser(githubId)));

        merge(signedIn, tokenService.asToken(other)).andExpect(status().isOk());

        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isEqualTo(signedIn.getId());
        assertThat(userRepository.existsById(other.getId())).isFalse();
    }

    @Test
    void theSurvivorKeepsItsOwnProfileValuesAndTakesTheOthersOnlyWhereItsOwnAreEmpty() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in").setBio("").setTwitter("own_twitter"));
        User other = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        userRepository.save(other.setEmail("other@example.com").setBio("Other bio").setTwitter("other_twitter")
                .setPrivacyPolicyAccepted(true));

        String survivorToken = merge(signedIn, tokenService.asToken(other))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/users/" + signedIn.getId()).header(AUTHORIZATION, "Bearer " + survivorToken))
                .andExpect(jsonPath("$.name").value("Signed in"))
                .andExpect(jsonPath("$.twitter").value("own_twitter"))
                .andExpect(jsonPath("$.email").value("other@example.com"))
                .andExpect(jsonPath("$.bio").value("Other bio"))
                .andExpect(jsonPath("$.privacyPolicyAccepted").value(true));
    }

    @Test
    void rolesCombineInTheSurvivorsFreshToken() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in").setVolunteer(true));
        User other = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        userRepository.save(other.setAdmin(true));

        String survivorToken = merge(signedIn, tokenService.asToken(other))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(tokenService.toUser(survivorToken))
                .returns(true, JelatynaPrincipal::isAdmin)
                .returns(true, JelatynaPrincipal::isVolunteer);
    }

    @Test
    void thePreviewShowsTheResultingProfileAndRoleChangeWithoutMerging() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in").setVolunteer(true));
        User other = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        userRepository.save(other.setEmail("other@example.com").setAdmin(true).setVolunteer(true));

        previewMerge(signedIn, tokenService.asToken(other))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Signed in"))
                .andExpect(jsonPath("$.email").value("other@example.com"))
                .andExpect(jsonPath("$.becomesAdmin").value(true))
                .andExpect(jsonPath("$.becomesVolunteer").value(false));

        assertThat(userRepository.existsById(other.getId())).isTrue();
        mockMvc.perform(get("/users/" + signedIn.getId()).header(AUTHORIZATION, bearer(signedIn)))
                .andExpect(jsonPath("$.email").isEmpty());
    }

    @Test
    void theOthersTalksMoveToTheSurvivorWhichStaysASpeakerOnceOfATalkTheyBothGive() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        User other = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        userRepository.save(other.setEmail("other@example.com"));
        talk("Other's talk", other);
        Presentation shared = talk("Shared talk", other, signedIn);

        String survivorToken = merge(signedIn, tokenService.asToken(other))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/users/" + signedIn.getId() + "/presentations").header(AUTHORIZATION, "Bearer " + survivorToken))
                .andExpect(jsonPath("$[*].title").value(containsInAnyOrder("Other's talk", "Shared talk")));
        mockMvc.perform(get("/presentations/" + shared.getId() + "/cospeakers").header(AUTHORIZATION, "Bearer " + survivorToken))
                .andExpect(jsonPath("$[*].id").value(contains(signedIn.getId())));
        assertThat(Jwts.parser().verifyWith(serverKey()).build().parseSignedClaims(survivorToken).getPayload()
                .get("isSpeaker", Boolean.class)).isTrue();
    }

    @Test
    void aSurvivorThatGainsItsFirstTalkIsASpeakerInItsFreshToken() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        User other = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        talk("Other's talk", other);
        assertThat(isSpeaker(tokenService.asToken(signedIn))).isFalse();

        String survivorToken = merge(signedIn, tokenService.asToken(other))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(isSpeaker(survivorToken)).isTrue();
    }

    private Boolean isSpeaker(String token) {
        return Jwts.parser().verifyWith(serverKey()).build().parseSignedClaims(token).getPayload().get("isSpeaker", Boolean.class);
    }

    @Test
    void thePreviewListsTheTalksThatWillMoveWithoutMovingThem() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        User other = oauthUserService.mapToUser(gitHubUser(uniqueId()));
        talk("Other's talk", other);
        // The survivor already speaks at it, so nothing moves.
        talk("Shared talk", other, signedIn);

        previewMerge(signedIn, tokenService.asToken(other))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.talksToMove").value(contains("Other's talk")));

        mockMvc.perform(get("/users/" + other.getId() + "/presentations").header(AUTHORIZATION, bearer(other)))
                .andExpect(jsonPath("$[*].title").value(containsInAnyOrder("Other's talk", "Shared talk")));
    }

    @Test
    void theOthersTicketMovesToTheSurvivorWhenOnlyTheOtherHasOne() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        User other = give(Content.TICKET, oauthUserService.mapToUser(gitHubUser(uniqueId())));

        String survivorToken = merge(signedIn, tokenService.asToken(other))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/users/" + signedIn.getId()).header(AUTHORIZATION, "Bearer " + survivorToken))
                .andExpect(jsonPath("$.participationData.id").value(other.getParticipationDataId()));
    }

    @Test
    void theSurvivorKeepsItsOwnTicketWhenTheOtherHasNone() throws Exception {
        User signedIn = give(Content.TICKET, userRepository.save(new User().setName("Signed in")));
        String secondToken = tokenService.asToken(oauthUserService.mapToUser(gitHubUser(uniqueId())));

        String survivorToken = merge(signedIn, secondToken)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/users/" + signedIn.getId()).header(AUTHORIZATION, "Bearer " + survivorToken))
                .andExpect(jsonPath("$.participationData.id").value(signedIn.getParticipationDataId()));
    }

    // The merge, and so its preview, refuses two tickets.
    @Test
    void whenBothUsersHaveATicketTheMergeIsRefusedWithAMessageToContactAnAdmin() throws Exception {
        User signedIn = give(Content.TICKET, userRepository.save(new User().setName("Signed in")));
        String githubId = uniqueId();
        User other = give(Content.TICKET, oauthUserService.mapToUser(gitHubUser(githubId)));

        merge(signedIn, tokenService.asToken(other))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("contact an admin")));
        previewMerge(signedIn, tokenService.asToken(other))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("contact an admin")));

        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isEqualTo(other.getId());
    }

    private User give(Content content, User user) {
        switch (content) {
            case SAVED_PROFILE -> userRepository.save(user.setPrivacyPolicyAccepted(true));
            case TALK -> talk("Talk", user);
            case ADMIN_ROLE -> userRepository.save(user.setAdmin(true));
            case VOLUNTEER_ROLE -> userRepository.save(user.setVolunteer(true));
            case TICKET -> {
                // Only an admin issues vouchers. No arrival date: other tests count who arrived.
                SecurityHelper.asAdmin();
                ParticipationData ticket = participationRepository.save(new ParticipationData().setVoucher(voucherService.generateVoucher("")));
                SecurityHelper.cleanSecurity();
                userRepository.save(user.setParticipationDataId(ticket.getId()));
            }
        }
        return user;
    }

    private Presentation talk(String title, User speaker, User... coSpeakers) {
        Presentation talk = Presentation.from(
                new PresentationRequest(title, "short", "long", "beginner", "Polish", new String[]{}), speaker, Set.of());
        Arrays.stream(coSpeakers).forEach(talk::setSpeaker);
        return presentationRepository.save(talk.setEdition(editionRepository.findCurrent()));
    }

    @Test
    void aSecondSignInThatWasAlreadyMergedIsNotFound() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        String secondToken = tokenService.asToken(oauthUserService.mapToUser(gitHubUser(uniqueId())));
        merge(signedIn, secondToken).andExpect(status().isOk());

        merge(signedIn, secondToken).andExpect(status().isNotFound());
    }

    @Test
    void eachLinkLogsOneLineWithIdsAndHowManyTalksMoved() throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in").setEmail("signed.in@example.com"));
        String githubId = uniqueId();
        User brandNew = oauthUserService.mapToUser(gitHubUser(githubId));
        talk("Other's talk", brandNew);
        talk("Shared talk", brandNew, signedIn);
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
                            tuple("loginId", login.getId()),
                            tuple("talksMoved", 1));
            assertThat(event.getFormattedMessage()).doesNotContain("Signed in", "signed.in@example.com", "Octo Cat", githubId);
        });
    }

    // A Manual User has no Login to sign in with, so only an admin can merge it, and either User may survive.
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void anAdminMergesAManualUserWithAUserThatHasALoginEitherWayRound(boolean manualSurvives) throws Exception {
        User admin = userRepository.save(new User().setName("Admin").setAdmin(true));
        User manual = userRepository.save(new User().setName("Added by an admin"));
        talk("Manual's talk", manual);
        String githubId = uniqueId();
        User signedIn = oauthUserService.mapToUser(gitHubUser(githubId));
        User survivor = manualSurvives ? manual : signedIn;
        User absorbed = manualSurvives ? signedIn : manual;

        adminMerge(admin, survivor, absorbed).andExpect(status().isNoContent());

        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isEqualTo(survivor.getId());
        assertThat(userRepository.existsById(absorbed.getId())).isFalse();
        mockMvc.perform(get("/users/" + survivor.getId() + "/presentations").header(AUTHORIZATION, bearer(admin)))
                .andExpect(jsonPath("$[*].title").value(contains("Manual's talk")));
    }

    @Test
    void anAdminMergeFillsTheSurvivorsProfileGapsCombinesRolesAndMovesTheTicket() throws Exception {
        User admin = userRepository.save(new User().setName("Admin").setAdmin(true));
        User survivor = userRepository.save(new User().setName("Survivor").setBio("").setVolunteer(true));
        User absorbed = give(Content.TICKET, userRepository.save(new User().setName("Absorbed")
                .setEmail("absorbed@example.com").setBio("Absorbed bio").setAdmin(true).setPrivacyPolicyAccepted(true)));

        adminMerge(admin, survivor, absorbed).andExpect(status().isNoContent());

        mockMvc.perform(get("/users/" + survivor.getId()).header(AUTHORIZATION, bearer(admin)))
                .andExpect(jsonPath("$.name").value("Survivor"))
                .andExpect(jsonPath("$.email").value("absorbed@example.com"))
                .andExpect(jsonPath("$.bio").value("Absorbed bio"))
                .andExpect(jsonPath("$.admin").value(true))
                .andExpect(jsonPath("$.volunteer").value(true))
                .andExpect(jsonPath("$.privacyPolicyAccepted").value(true))
                .andExpect(jsonPath("$.participationData.id").value(absorbed.getParticipationDataId()));
    }

    @Test
    void anAdminCannotMergeTwoUsersWithATicketNorAUserWithItselfNorIntoAMissingUser() throws Exception {
        User admin = userRepository.save(new User().setName("Admin").setAdmin(true));
        User survivor = give(Content.TICKET, userRepository.save(new User().setName("Survivor")));
        User absorbed = give(Content.TICKET, userRepository.save(new User().setName("Absorbed")));

        adminMerge(admin, survivor, absorbed)
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Both users have a ticket")));
        // Both hold a ticket too, so only the reason shows which refusal answered.
        adminMerge(admin, survivor, survivor)
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("cannot be merged with itself")));
        adminMerge(admin, new User(), absorbed).andExpect(status().isNotFound());

        assertThat(userRepository.existsById(absorbed.getId())).isTrue();
        assertThat(userRepository.existsById(survivor.getId())).isTrue();
    }

    @Test
    void eachAdminMergeLogsOneLineWithIdsMarkedAsDoneByAnAdmin() throws Exception {
        User admin = userRepository.save(new User().setName("Admin").setAdmin(true));
        User survivor = userRepository.save(new User().setName("Survivor").setEmail("survivor@example.com"));
        String githubId = uniqueId();
        User absorbed = oauthUserService.mapToUser(gitHubUser(githubId));
        talk("Absorbed's talk", absorbed);
        Logger logger = (Logger) LoggerFactory.getLogger(UserLoginsController.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            adminMerge(admin, survivor, absorbed).andExpect(status().isNoContent());
        } finally {
            logger.detachAppender(appender);
        }

        assertThat(appender.list).singleElement().satisfies(event -> {
            assertThat(event.getKeyValuePairs())
                    .extracting(pair -> pair.key, pair -> pair.value)
                    .containsExactlyInAnyOrder(
                            tuple("actorUserId", admin.getId()),
                            tuple("byAdmin", true),
                            tuple("survivorUserId", survivor.getId()),
                            tuple("absorbedUserId", absorbed.getId()),
                            tuple("loginsMoved", 1),
                            tuple("talksMoved", 1));
            assertThat(event.getFormattedMessage()).doesNotContain("Survivor", "survivor@example.com", "Octo Cat", githubId);
        });
    }

    @Test
    void anAdminPreviewsWhatTheMergeWouldMakeOfTheSurvivorWithoutMerging() throws Exception {
        User admin = userRepository.save(new User().setName("Admin").setAdmin(true));
        User survivor = userRepository.save(new User().setName("Survivor").setVolunteer(true));
        User absorbed = userRepository.save(new User().setName("Absorbed").setEmail("absorbed@example.com")
                .setAdmin(true).setVolunteer(true));
        talk("Absorbed's talk", absorbed);

        adminPreviewMerge(admin, survivor, absorbed)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Survivor"))
                .andExpect(jsonPath("$.email").value("absorbed@example.com"))
                .andExpect(jsonPath("$.becomesAdmin").value(true))
                .andExpect(jsonPath("$.becomesVolunteer").value(false))
                .andExpect(jsonPath("$.talksToMove").value(contains("Absorbed's talk")));

        assertThat(userRepository.existsById(absorbed.getId())).isTrue();
        mockMvc.perform(get("/users/" + survivor.getId()).header(AUTHORIZATION, bearer(admin)))
                .andExpect(jsonPath("$.email").isEmpty());
    }

    // The admin preview refuses exactly what the admin merge refuses, with the same reasons.
    @Test
    void theAdminPreviewRefusesTwoTicketsAUserWithItselfAMissingUserAndANonAdmin() throws Exception {
        User admin = userRepository.save(new User().setName("Admin").setAdmin(true));
        User survivor = give(Content.TICKET, userRepository.save(new User().setName("Survivor")));
        User absorbed = give(Content.TICKET, userRepository.save(new User().setName("Absorbed")));

        adminPreviewMerge(admin, survivor, absorbed)
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Both users have a ticket")));
        adminPreviewMerge(admin, survivor, survivor)
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("cannot be merged with itself")));
        adminPreviewMerge(admin, new User(), absorbed).andExpect(status().isNotFound());
        adminPreviewMerge(admin, survivor, new User()).andExpect(status().isNotFound());
        adminPreviewMerge(survivor, survivor, absorbed).andExpect(status().isForbidden());
    }

    @Test
    void onlyAnAdminMergesUsersByTheirIds() throws Exception {
        User signedIn = userRepository.save(new User().setName("Not an admin"));
        User other = userRepository.save(new User().setName("Other"));

        adminMerge(signedIn, signedIn, other).andExpect(status().isForbidden());

        assertThat(userRepository.existsById(other.getId())).isTrue();
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

    @ParameterizedTest
    @EnumSource
    void cancellingLeavesAUserWithContentAlone(Content content) throws Exception {
        User signedIn = userRepository.save(new User().setName("Signed in"));
        String githubId = uniqueId();
        User other = give(content, oauthUserService.mapToUser(gitHubUser(githubId)));

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

    @Test
    void aUserRemovesOneOfTheirLoginsAndSigningInWithItLaterStartsANewUser() throws Exception {
        User user = userRepository.save(new User().setName("Signed in twice"));
        String githubId = uniqueId();
        Login github = loginRepository.save(new Login(user, "github", githubId));
        loginRepository.save(new Login(user, "google", uniqueId()));

        unlink(user, user, github).andExpect(status().isNoContent());

        mockMvc.perform(get("/users/" + user.getId() + "/logins").header(AUTHORIZATION, bearer(user)))
                .andExpect(jsonPath("$[*].provider").value(containsInAnyOrder("google")));
        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isNotEqualTo(user.getId());
    }

    @Test
    void aUserCannotRemoveTheirLastLogin() throws Exception {
        User user = userRepository.save(new User().setName("Signed in once"));
        String githubId = uniqueId();
        Login github = loginRepository.save(new Login(user, "github", githubId));

        unlink(user, user, github).andExpect(status().isConflict());

        assertThat(oauthUserService.mapToUser(gitHubUser(githubId)).getId()).isEqualTo(user.getId());
    }

    @Test
    void anAdminRemovesAnyUsersLoginIncludingTheLast() throws Exception {
        User admin = userRepository.save(new User().setName("Admin").setAdmin(true));
        User user = userRepository.save(new User().setName("Signed in once"));
        Login github = loginRepository.save(new Login(user, "github", uniqueId()));

        unlink(admin, user, github).andExpect(status().isNoContent());

        mockMvc.perform(get("/users/" + user.getId() + "/logins").header(AUTHORIZATION, bearer(admin)))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void anotherUsersLoginCannotBeRemoved() throws Exception {
        User owner = userRepository.save(new User().setName("Owner"));
        User someoneElse = userRepository.save(new User().setName("Someone else"));
        Login github = loginRepository.save(new Login(owner, "github", uniqueId()));
        loginRepository.save(new Login(owner, "google", uniqueId()));

        unlink(someoneElse, owner, github).andExpect(status().isForbidden());
        // Their own id in the path does not reach another User's Login either.
        mockMvc.perform(delete("/users/" + someoneElse.getId() + "/logins/" + github.getId())
                        .header(AUTHORIZATION, bearer(someoneElse)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/users/" + owner.getId() + "/logins").header(AUTHORIZATION, bearer(owner)))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void eachUnlinkLogsOneLineWithIdsOnly() throws Exception {
        User admin = userRepository.save(new User().setName("Admin").setAdmin(true));
        User user = userRepository.save(new User().setName("Signed in").setEmail("signed.in@example.com"));
        String githubId = uniqueId();
        Login github = loginRepository.save(new Login(user, "github", githubId));
        Logger logger = (Logger) LoggerFactory.getLogger(UserLoginsController.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            unlink(admin, user, github).andExpect(status().isNoContent());
        } finally {
            logger.detachAppender(appender);
        }

        assertThat(appender.list).singleElement().satisfies(event -> {
            assertThat(event.getKeyValuePairs())
                    .extracting(pair -> pair.key, pair -> pair.value)
                    .containsExactlyInAnyOrder(
                            tuple("actorUserId", admin.getId()),
                            tuple("userId", user.getId()),
                            tuple("provider", "github"),
                            tuple("loginId", github.getId()));
            assertThat(event.getFormattedMessage()).doesNotContain("Signed in", "signed.in@example.com", githubId);
        });
    }

    private ResultActions unlink(User actor, User owner, Login login) throws Exception {
        return mockMvc.perform(delete("/users/" + owner.getId() + "/logins/" + login.getId())
                .header(AUTHORIZATION, bearer(actor)));
    }

    private ResultActions cancelMerge(User signedIn, String secondToken) throws Exception {
        return mockMvc.perform(post("/users/merge/cancel")
                .header(AUTHORIZATION, bearer(signedIn))
                .contentType(APPLICATION_JSON)
                .content(json(new UserLoginsController.MergeUsersRequest(secondToken))));
    }

    private ResultActions previewMerge(User signedIn, String secondToken) throws Exception {
        return mockMvc.perform(post("/users/merge/preview")
                .header(AUTHORIZATION, bearer(signedIn))
                .contentType(APPLICATION_JSON)
                .content(json(new UserLoginsController.MergeUsersRequest(secondToken))));
    }

    private ResultActions adminMerge(User actor, User survivor, User absorbed) throws Exception {
        return mockMvc.perform(post("/users/" + survivor.getId() + "/merge/" + absorbed.getId())
                .header(AUTHORIZATION, bearer(actor)));
    }

    private ResultActions adminPreviewMerge(User actor, User survivor, User absorbed) throws Exception {
        return mockMvc.perform(get("/users/" + survivor.getId() + "/merge/" + absorbed.getId() + "/preview")
                .header(AUTHORIZATION, bearer(actor)));
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
