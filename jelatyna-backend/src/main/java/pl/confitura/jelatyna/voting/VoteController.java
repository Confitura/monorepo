package pl.confitura.jelatyna.voting;

import static java.util.stream.Collectors.toList;

import com.google.common.collect.Lists;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.infrastructure.WebUtils;
import pl.confitura.jelatyna.presentation.*;
import pl.confitura.jelatyna.user.User;

@RestController
public class VoteController {

    private PresentationRepository presentationRepository;
    private VoteRepository voteRepository;
    private WebUtils webUtils;
    private LocalValidatorFactoryBean beanValidator;
    private TransactionTemplate transactionTemplate;

    @Autowired
    public VoteController(PresentationRepository presentationRepository, VoteRepository voteRepository,
                          WebUtils webUtils, LocalValidatorFactoryBean beanValidator,
                          TransactionTemplate transactionTemplate) {
        this.presentationRepository = presentationRepository;
        this.voteRepository = voteRepository;
        this.webUtils = webUtils;
        this.beanValidator = beanValidator;
        this.transactionTemplate = transactionTemplate;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(beanValidator);
    }

    @RequestMapping(value = "/votes/start/{token}", method = RequestMethod.POST)
    public ResponseEntity<List<InlineVote>> start(@PathVariable String token) {
        try {
            return ResponseEntity.ok(transactionTemplate.execute(status -> InlineVote.from(votesFor(token))));
        } catch (DataIntegrityViolationException startedAtTheSameTime) {
            // A concurrent start for the token saved its votes first, and the unique (token, talk) index refused
            // these. That rolled this start's transaction back, so the other start's votes are read in a new one.
            return ResponseEntity.ok(transactionTemplate.execute(status ->
                    InlineVote.from(voteRepository.findAllForTokenInCurrentEdition(token))));
        }
    }

    private List<Vote> votesFor(String token) {
        List<Vote> votes = voteRepository.findAllForTokenInCurrentEdition(token);
        return votes.isEmpty() ? generateVotes(token) : votes;
    }

    private List<Vote> generateVotes(String token) {
        List<Presentation> presentations = Lists.newArrayList(this.presentationRepository.findAllForV4p());
        Collections.shuffle(presentations);
        List<Vote> votes = IntStream.range(0, presentations.size())
                .mapToObj(idx -> new Vote()
                        .setClient(webUtils.getClientIp())
                        .setToken(token)
                        .setPresentation(presentations.get(idx))
                        .setOrder(idx))
                // Saved in talk order, so a concurrent start for the token waits on the same first vote and
                // then hits the unique (token, talk) index, instead of the two deadlocking.
                .sorted(Comparator.comparing((Vote vote) -> vote.getPresentation().getId()))
                .collect(toList());
        voteRepository.saveAll(votes);
        return voteRepository.findAllForTokenInCurrentEdition(token);
    }

    @PostMapping("/votes")
    @Transactional
    public ResponseEntity<InlineVote> save(@RequestBody @Valid VoteController.VoteRequest vote) {
        Vote loaded = voteRepository.findById(vote.id());
        if (!loaded.getPresentation().getEdition().isCurrent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        loaded.setRate(vote.rate);
        loaded.setVoteDate(LocalDateTime.now());
        return ResponseEntity.ok(InlineVote.from(loaded));
    }


    @PreAuthorize("@security.isAdmin()")
    @GetMapping("/votes/statistics")
    @ResponseBody
    @Transactional
    public List<PresentationStats> statistics() {
        Iterable<Vote> allVotes = voteRepository.findAllInCurrentEdition();
        Stream<Vote> votesStream = StreamSupport.stream(allVotes.spliterator(), true);

        Map<String, Optional<PresentationStats>> statsByPresentation = votesStream
                .map(PresentationStats::new)
                .collect(Collectors.groupingBy(PresentationStats::getPresentationId,
                        Collectors.reducing(PresentationStats::add)));

        return statsByPresentation.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(PresentationStats::getPositiveVotes))
                .collect(toList());

    }

    @PreAuthorize("@security.isAdmin()")
    @GetMapping("/votes/results")
    @ResponseBody
    @Transactional
    public List<VoteResult> results(@RequestParam(required = false) List<String> tokens) {
        Set<String> selectedTokens = tokens == null ? Set.of() : new HashSet<>(tokens);

        Set<String> preSelectedSpeakerIds = presentationRepository.findAllInCurrentEdition().stream()
                .filter(presentation -> presentation.getPreSelectionStatus() == PreSelectionStatus.PRE_APPROVED)
                .flatMap(presentation -> presentation.getSpeakers().stream())
                .map(User::getId)
                .collect(Collectors.toSet());

        Map<String, List<Vote>> votesByPresentation = StreamSupport
                .stream(voteRepository.findAllInCurrentEdition().spliterator(), false)
                .filter(vote -> vote.getRate() != null)
                .collect(Collectors.groupingBy(vote -> vote.getPresentation().getId()));

        return votesByPresentation.values().stream()
                .map(votes -> VoteResult.from(votes.get(0).getPresentation(), votes, selectedTokens, preSelectedSpeakerIds))
                .sorted(Comparator.comparingInt(VoteResult::score).reversed())
                .collect(toList());
    }

    public record VoteRequest(
            String id,
            Integer rate
    ) {

    }

    public record InlineVote(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Integer order,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            InlineVotePresentation presentation,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Integer rate
    ) {

        public static InlineVote from(Vote loaded) {
            return new InlineVote(
                    loaded.getId(),
                    loaded.getOrder(),
                    InlineVotePresentation.from(loaded.getPresentation()),
                    loaded.getRate()
            );
        }

        public static List<InlineVote> from(List<Vote> votes) {
            return votes.stream().map(InlineVote::from).toList();
        }
    }

    public record InlineVotePresentation(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String title,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String longDescription,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String shortDescription,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<InlineVoteSpeaker> speakers,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean workshop
    ) {
        public static InlineVotePresentation from(Presentation presentation) {
            return new InlineVotePresentation(
                    presentation.getId(),
                    presentation.getTitle(),
                    presentation.getDescription(),
                    presentation.getShortDescription(),
                    InlineVoteSpeaker.from(presentation.getSpeakers()),
                    presentation.isWorkshop()
            );
        }
    }

    public record InlineVoteSpeaker(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String name,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String bio,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String photo
    ) {
        public static List<InlineVoteSpeaker> from(Set<User> speakers) {
            return speakers.stream().map(InlineVoteSpeaker::from).toList();
        }

        public static InlineVoteSpeaker from(User speaker) {
            return new InlineVoteSpeaker(
                    speaker.getId(),
                    speaker.getName(),
                    speaker.getBio(),
                    speaker.getPhoto()
            );
        }
    }
}
