package pl.confitura.jelatyna.presentation.like;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.presentation.Presentation;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping
@RequiredArgsConstructor
class LikeController {

    private final LikeRepository likeRepository;

    @PostMapping("/presentations/{presentationId}/likes")
    public ResponseEntity createVote(
            @PathVariable("presentationId") Presentation presentation,
            @Valid @RequestBody Like like
    ) {
        if (likeRepository.findByPresentationAndToken(presentation, like.token) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        like.setPresentation(presentation);
        likeRepository.save(like);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/presentations/{presentationId}/likes")
    @PreAuthorize("@security.isAdmin()")
    public Long countByPresentation(
            @PathVariable("presentationId") Presentation presentation
    ) {
        return likeRepository.countByPresentation(presentation);
    }

    @DeleteMapping("/likes/{id}")
    public void delete(@PathVariable("id") String likeId) {
        likeRepository.deleteById(likeId);
    }

    @GetMapping("/likes/summary")
    @PreAuthorize("@security.isAdmin()")
    public Map<String, Long> getLikesSummary() {
        return likeRepository.findAll()
                .collect(groupingBy(
                        it -> it.getPresentation().getId(),
                        counting()
                ));


    }

    @GetMapping("/likes")
    public List<LikeResponse> getLikes(String token) {
        List<Like> likes = likeRepository.findByToken(token);
        return likes.stream()
                .map(LikeResponse::new)
                .collect(toList());
    }

    @Data
    static class LikeResponse {
        String id;
        String presentationId;

        LikeResponse(Like like) {
            this.id = like.getId();
            this.presentationId = like.getPresentation().getId();
        }
    }
}
