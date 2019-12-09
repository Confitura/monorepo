package pl.confitura.jelatyna.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.user.dto.FullUserDto;
import pl.confitura.jelatyna.user.dto.PublicUserDto;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserFacade {

    private final UserRepository userRepository;

    public boolean existsUserWithSocialId(String socialId) {
        return userRepository.existsBySocialId(socialId);
    }

    public FullUserDto save(FullUserDto dtoUser) {
        if (dtoUser != null) {
            UserEntity user = UserEntity.fromDto(dtoUser);
            return toDto(userRepository.save(user));
        } else {
            return null;
        }
    }

    public FullUserDto findBySocialId(String socialId) {
        return toDto(userRepository.findBySocialId(socialId));
    }

    public FullUserDto findById(String id) {
        return toDto(userRepository.findById(id));
    }

    public FullUserDto findByEmail(String email) {
        return toDto(userRepository.findByEmail(email));
    }

    private FullUserDto toDto(UserEntity user) {
        return user == null ? null : user.toDto();
    }

    public void changePhoto(String userId, String path) {
        UserEntity user = userRepository.findById(userId);
        user.setPhoto(path);
        userRepository.save(user);
    }

    public void markSpeaker(String id, boolean isSpeaker) {
        UserEntity user = userRepository.findById(id);
        user.setSpeaker(isSpeaker);
        userRepository.save(user);
    }

    public Set<PublicUserDto> findAdmins() {
        return userRepository.findAdmins().stream()
                .map(it -> it.toDto().toPublicUser())
                .collect(Collectors.toSet());
    }

    public Set<PublicUserDto> findVolunteers() {
        return userRepository.findVolunteers().stream()
                .map(it -> it.toDto().toPublicUser())
                .collect(Collectors.toSet());
    }

    public Set<PublicUserDto> findAcceptedSpeakers() {
        return userRepository.findAllAccepted().stream()
                .map(it -> it.toDto().toPublicUser())
                .collect(Collectors.toSet());
    }

    public Set<FullUserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserEntity::toDto)
                .collect(Collectors.toSet());
    }
}
