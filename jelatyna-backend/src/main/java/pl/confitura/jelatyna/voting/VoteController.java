package pl.confitura.jelatyna.voting;

import static java.util.stream.Collectors.toList;

import com.google.common.collect.Lists;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    public VoteController(PresentationRepository presentationRepository, VoteRepository voteRepository,
                          WebUtils webUtils, LocalValidatorFactoryBean beanValidator) {
        this.presentationRepository = presentationRepository;
        this.voteRepository = voteRepository;
        this.webUtils = webUtils;
        this.beanValidator = beanValidator;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(beanValidator);
    }

    @RequestMapping(value = "/votes/start/{token}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<List<InlineVote>> start(@PathVariable String token) {
        List<Vote> votes = voteRepository.findAllForToken(token);
        if (votes.isEmpty()) {
            votes = generateVotes(token);
        }
        return ResponseEntity.ok(InlineVote.from(votes));
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
                .collect(toList());
        voteRepository.saveAll(votes);
        return voteRepository.findAllForToken(token);
    }

    @PostMapping("/votes")
    @Transactional
    public ResponseEntity<InlineVote> save(@RequestBody @Valid VoteController.VoteRequest vote) {
        Vote loaded = voteRepository.findById(vote.id());
        loaded.setRate(vote.rate);
        loaded.setVoteDate(LocalDateTime.now());
        return ResponseEntity.ok(InlineVote.from(loaded));
    }


    @PreAuthorize("@security.isAdmin()")
    @GetMapping("/votes/statistics")
    @ResponseBody
    @Transactional
    public List<PresentationStats> statistics() {
        Iterable<Vote> allVotes = voteRepository.findAll();
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

    public record VoteRequest(
            String id,
            Integer rate
    ) {

    }

    public record InlineVote(
            String id,
            Integer order,
            InlineVotePresentation presentation,
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
            String id,
            String title,
            String longDescription,
            String shortDescription,
            List<InlineVoteSpeaker> speakers
    ) {
        public static InlineVotePresentation from(Presentation presentation) {
            return new InlineVotePresentation(
                    presentation.getId(),
                    presentation.getTitle(),
                    presentation.getDescription(),
                    presentation.getShortDescription(),
                    InlineVoteSpeaker.from(presentation.getSpeakers())
            );
        }
    }

    public record InlineVoteSpeaker(
            String id,
            String name,
            String bio,
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
