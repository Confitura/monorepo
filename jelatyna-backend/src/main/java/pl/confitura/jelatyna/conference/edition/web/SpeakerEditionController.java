package pl.confitura.jelatyna.conference.edition.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pl.confitura.jelatyna.edition.EditionDto;
import pl.confitura.jelatyna.edition.EditionRepository;

/** The Editions for a speaker's homepage picker, under their /users/{id} path. */
@RestController
@RequiredArgsConstructor
public class SpeakerEditionController {

    private final EditionRepository editionRepository;

    /** The Current Edition and every Edition the user has a talk or workshop in, newest year first. */
    @GetMapping("/users/{id}/editions")
    @PreAuthorize("@security.isOwner(#id)")
    public List<EditionDto> getUserEditions(@PathVariable String id) {
        return editionRepository.findCurrentAndSpokenInBy(id).stream().map(EditionDto::from).toList();
    }
}
