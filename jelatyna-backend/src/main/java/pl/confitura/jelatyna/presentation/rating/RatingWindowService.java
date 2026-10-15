package pl.confitura.jelatyna.presentation.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * The global rating window derives from the agenda: rating opens at the start of
 * the conference's first day (the earliest agenda {@link Day} date), Europe/Warsaw,
 * and stays open afterwards. There is no separate flag — admins control it by
 * setting the agenda dates.
 */
@Service
@RequiredArgsConstructor
public class RatingWindowService {

    private static final ZoneId ZONE = ZoneId.of("Europe/Warsaw");

    private final DayRepository dayRepository;

    public Optional<LocalDate> opensAt() {
        return dayRepository.findAll().stream()
                .map(Day::getDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());
    }

    public boolean isOpen() {
        return opensAt()
                .map(date -> !LocalDate.now(ZONE).isBefore(date))
                .orElse(false);
    }
}
