package pl.confitura.jelatyna.presentation.rating;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.confitura.jelatyna.agenda.Day;
import pl.confitura.jelatyna.agenda.DayRepository;

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

    private Day day(String id, LocalDate date) {
        return new Day().setId(id).setDate(date).setLabel(id).setDisplayOrder(1);
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
}
