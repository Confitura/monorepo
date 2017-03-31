package pl.confitura.jelatyna.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.timgroup.jgravatar.Gravatar;
import com.timgroup.jgravatar.GravatarDefaultImage;
import com.timgroup.jgravatar.GravatarRating;

@RepositoryRestController
public class UserController {

    private UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/users")
    public ResponseEntity<?> save(@RequestBody User user) {
        if (StringUtils.isEmpty(user.getPhoto())){
            Gravatar gravatar = new Gravatar(300, GravatarRating.GENERAL_AUDIENCES, GravatarDefaultImage.BLANK );
            String url = gravatar.getUrl(user.getEmail());
            user.setPhoto(url);
        }
        User saved = repository.save(user);
        return ResponseEntity.ok(new Resource<>(saved));
    }
}
