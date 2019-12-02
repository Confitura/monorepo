package pl.confitura.jelatyna.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.user.dto.PublicUser;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserFacade {

    private final UserRepository userRepository;

    public boolean existsUserWithSocialId(String socialId) {
        return userRepository.existsBySocialId(socialId);
    }

    public pl.confitura.jelatyna.user.dto.User save(pl.confitura.jelatyna.user.dto.User dtoUser) {
        if (dtoUser != null) {
            User user = User.fromDto(dtoUser);
            return toDto(userRepository.save(user));
        } else {
            return null;
        }
    }

    public pl.confitura.jelatyna.user.dto.User findBySocialId(String socialId) {
        return toDto(userRepository.findBySocialId(socialId));
    }

    public pl.confitura.jelatyna.user.dto.User findById(String id) {
        return toDto(userRepository.findById(id));
    }

    public pl.confitura.jelatyna.user.dto.User findByEmail(String email) {
        return toDto(userRepository.findByEmail(email));
    }

    private pl.confitura.jelatyna.user.dto.User toDto(User user) {
        return user == null ? null : user.toDto();
    }

    public void changePhoto(String userId, String path) {
        User user = userRepository.findById(userId);
        user.setPhoto(path);
        userRepository.save(user);
    }

    public void markSpeaker(String id, boolean isSpeaker) {
        User user = userRepository.findById(id);
        user.setSpeaker(isSpeaker);
        userRepository.save(user);
    }

    public Set<PublicUser> findAdmins() {
        return userRepository.findAdmins().stream()
                .map(it -> it.toDto().toPublicUser())
                .collect(Collectors.toSet());
    }

    public Set<PublicUser> findVolunteers() {
        return userRepository.findVolunteers().stream()
                .map(it -> it.toDto().toPublicUser())
                .collect(Collectors.toSet());
    }

    public Set<PublicUser> findAcceptedSpeakers() {
        return userRepository.findAllAccepted().stream()
                .map(it -> it.toDto().toPublicUser())
                .collect(Collectors.toSet());
    }

    public Set<pl.confitura.jelatyna.user.dto.User> findAll() {
        return userRepository.findAll().stream()
                .map(User::toDto)
                .collect(Collectors.toSet());
    }
}
