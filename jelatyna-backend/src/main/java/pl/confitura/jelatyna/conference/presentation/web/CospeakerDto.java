package pl.confitura.jelatyna.conference.presentation.web;

import pl.confitura.jelatyna.identity.user.domain.User;

/** A talk's speaker as the co-speaker dialog shows them: name and email, and the id to remove them by. */
public record CospeakerDto(String id, String name, String email) {

    static CospeakerDto from(User user) {
        return new CospeakerDto(user.getId(), user.getName(), user.getEmail());
    }
}
