package pl.confitura.jelatyna.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<Resource<Presentation>> addPresentationToUser(@RequestBody Presentation presentation, @PathVariable String userId) {
        System.out.println(userId);
        User speaker = repository.getOne(userId);
        presentation.setSpeaker(speaker);
        Presentation saved = presentationRepository.save(presentation);
        return ResponseEntity.ok(new Resource<>(saved));
    }
}
