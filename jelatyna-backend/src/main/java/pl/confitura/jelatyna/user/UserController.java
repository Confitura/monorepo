package pl.confitura.jelatyna.user;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;

@RepositoryRestController
public class UserController {

    private UserRepository repository;
    private PresentationRepository presentationRepository;

    @Autowired
    public UserController(UserRepository repository, PresentationRepository presentationRepository) {
        this.repository = repository;
        this.presentationRepository = presentationRepository;
    }

    @PostMapping("/users")
    @PreAuthorize("@security.isOwner(#user.id)")
    public ResponseEntity<?> save(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getPhoto())) {
            Gravatar gravatar = new Gravatar(300, GravatarRating.GENERAL_AUDIENCES, GravatarDefaultImage.BLANK);
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url);
        }
        User saved = repository.save(user);
        return ResponseEntity.ok(new Resource<>(saved));
    }

    @PostMapping("/users/{userId}/presentations")
    @PreAuthorize("@security.isOwner(#userId)")
    public ResponseEntity<?> addPresentationToUser(@Valid @RequestBody Presentation presentation,
            @PathVariable String userId) {
        User speaker = repository.findOne(userId);
        presentation.setSpeaker(speaker);
        Presentation saved = presentationRepository.save(presentation);
        return ResponseEntity.ok(new Resource<>(saved));
    }
}
