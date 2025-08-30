package pl.confitura.jelatyna.agenda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import org.springframework.stereotype.Service;
import pl.confitura.jelatyna.presentation.Presentation;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IcalExportService {

    private final AgendaService agendaService;
    private final DayRepository dayRepository;

    public byte[] generateIcs() {
        Iterable<AgendaEntry> entries = agendaService.findAllAndMerge();
        Calendar calendar = buildCalendar(entries);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new CalendarOutputter().output(calendar, out);
            return out.toString(StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate iCal", e);
        }
    }

    Calendar buildCalendar(Iterable<AgendaEntry> entries) {
        Calendar calendar = new Calendar();
        calendar.withProperty(new ProdId("-//Jelatyna//Conference Schedule//EN"));

        ZoneId zone = ZoneId.of("Europe/Warsaw"); // using conference local timezone

        for (AgendaEntry entry : entries) {
            if (entry.getTimeSlot() == null || entry.getTimeSlot().getStart() == null || entry.getTimeSlot().getEnd() == null) {
                continue;
            }
            String dayId = entry.getTimeSlot().getId().dayId();
            Day day = dayRepository.findById(dayId);
            if (day == null || day.getDate() == null) {
                continue;
            }
            LocalDate date = day.getDate();
            LocalDateTime startLdt = LocalDateTime.of(date, entry.getTimeSlot().getStart());
            LocalDateTime endLdt = LocalDateTime.of(date, entry.getTimeSlot().getEnd());
            ZonedDateTime start = startLdt.atZone(zone);
            ZonedDateTime end = endLdt.atZone(zone);

            String summary = buildSummary(entry);
            VEvent event = new VEvent();
            event.withProperty(new Summary(summary));
            event.withProperty(new DtStart<>(start));
            event.withProperty(new DtEnd<>(end));

            if (entry.getRoom() != null && entry.getRoom().getLabel() != null) {
                event.withProperty(new Location(entry.getRoom().getLabel()));
            } else {
                log.info("No room for entry: {}", entry.getId());
            }

            String description = buildDescription(entry);
            if (description != null && !description.isBlank()) {
                event.withProperty(new Description(description));
            }

            event.withProperty(new Uid(UUID.nameUUIDFromBytes((entry.getId() == null ? (summary + start) : entry.getId()).getBytes(StandardCharsets.UTF_8)).toString()));

            calendar.withComponent(event);
        }
        return calendar;
    }

    private static String buildSummary(AgendaEntry entry) {
        if (entry.getPresentation() != null && entry.getPresentation().getTitle() != null) {
            return entry.getPresentation().getTitle();
        }
        if (entry.getLabel() != null) {
            return entry.getLabel();
        }
        return "Agenda Entry";
    }

    private static String buildDescription(AgendaEntry entry) {
        Presentation p = entry.getPresentation();
        if (p != null) {
            if (p.getShortDescription() != null && !p.getShortDescription().isBlank()) {
                return p.getShortDescription();
            }
            if (p.getDescription() != null && !p.getDescription().isBlank()) {
                return p.getDescription();
            }
        }
        return entry.getLabel();
    }
}
