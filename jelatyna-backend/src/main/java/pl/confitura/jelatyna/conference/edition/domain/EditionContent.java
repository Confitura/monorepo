package pl.confitura.jelatyna.conference.edition.domain;

import pl.confitura.jelatyna.edition.Edition;

/** Something an Edition owns, such as its talks or agenda Days; an Edition that owns any can't be deleted. */
public interface EditionContent {

    boolean existsByEdition(Edition edition);
}
