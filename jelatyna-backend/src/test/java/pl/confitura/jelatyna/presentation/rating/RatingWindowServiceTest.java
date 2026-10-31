package pl.confitura.jelatyna.presentation.rating;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.conference.edition.domain.Edition;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingWindowServiceTest {

    @Mock
    DayRepository dayRepository;

    @InjectMocks
    RatingWindowService service;

    private static final Edition CURRENT_EDITION = new Edition().setYear(2027).setCurrent(true);
    private static final Edition PAST_EDITION = new Edition().setYear(2026);

    private Day day(String id, LocalDate date) {
        return day(id, date, CURRENT_EDITION);
    }

    private Day day(String id, LocalDate date, Edition edition) {
        return new Day().setId(id).setDate(date).setLabel(id).setDisplayOrder(1).setEdition(edition);
    }

    @Test
    void isClosedWhenNoAgendaDays() {
        when(dayRepository.findAll()).thenReturn(List.of());

        assertThat(service.opensAt()).isEmpty();
        assertThat(service.isOpen()).isFalse();
    }

    @Test
    void isOpenOnAndAfterTheFirstDay() {
        when(dayRepository.findAll()).thenReturn(List.of(
                day("day-1", LocalDate.now().minusDays(1)),
                day("day-2", LocalDate.now())
        ));

        assertThat(service.isOpen()).isTrue();
    }

    @Test
    void isClosedBeforeTheFirstDay() {
        when(dayRepository.findAll()).thenReturn(List.of(
                day("day-1", LocalDate.now().plusDays(2)),
                day("day-2", LocalDate.now().plusDays(3))
        ));

        assertThat(service.isOpen()).isFalse();
    }

    @Test
    void opensAtIsTheEarliestDay() {
        LocalDate earliest = LocalDate.of(2026, 9, 25);
        when(dayRepository.findAll()).thenReturn(List.of(
                day("day-2", LocalDate.of(2026, 9, 26)),
                day("day-1", earliest)
        ));

        assertThat(service.opensAt()).contains(earliest);
    }

    @Test
    void opensOnTheCurrentEditionsFirstDayIgnoringOtherEditions() {
        LocalDate firstDay = LocalDate.now().plusDays(2);
        when(dayRepository.findAll()).thenReturn(List.of(
                day("past-edition-day", LocalDate.now().minusYears(1), PAST_EDITION),
                day("day-1", firstDay)
        ));

        assertThat(service.opensAt()).contains(firstDay);
        assertThat(service.isOpen()).isFalse();
    }
}
