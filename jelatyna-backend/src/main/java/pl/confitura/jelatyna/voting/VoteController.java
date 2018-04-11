package pl.confitura.jelatyna.voting;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Lists;
import pl.confitura.jelatyna.infrastructure.WebUtils;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

@RepositoryRestController
public class VoteController {

    private PresentationRepository presentationRepository;
    private VoteRepository voteRepository;
    private WebUtils webUtils;

    @Autowired
    public VoteController(PresentationRepository presentationRepository, VoteRepository voteRepository, WebUtils webUtils) {
        this.presentationRepository = presentationRepository;
        this.voteRepository = voteRepository;
        this.webUtils = webUtils;
    }

    @RequestMapping(value = "/votes/start/{token}", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Resources<?>> start(@PathVariable String token) {
        Set<Vote> votes = voteRepository.findAllForToken(token);
        if (votes.isEmpty()) {
            votes = generateVotes(token);
        }
        return ResponseEntity.ok(new Resources<>(votes));
    }

    private Set<Vote> generateVotes(@PathVariable String token) {
        List<Presentation> presentations = Lists.newArrayList(this.presentationRepository.findAllForV4p());
        Collections.shuffle(presentations);
        List<Vote> votes = IntStream.range(0, presentations.size())
                .mapToObj(idx -> new Vote()
                        .setClient(webUtils.getClientIp())
                        .setToken(token)
                        .setPresentation(presentations.get(idx))
                        .setOrder(idx))
                .collect(toList());
        return this.voteRepository.save(votes);
    }

    @PostMapping("/votes")
    @Transactional
    public ResponseEntity<Resource<Vote>> save(@RequestBody Vote vote) {
        Vote loaded = voteRepository.findById(vote.getId());
        loaded.setRate(vote.getRate());
        loaded.setVoteDate(LocalDateTime.now());
        return ResponseEntity.ok(new Resource<>(loaded));
    }
}
