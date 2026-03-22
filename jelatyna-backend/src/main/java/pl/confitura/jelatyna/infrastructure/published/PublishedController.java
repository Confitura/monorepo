package pl.confitura.jelatyna.infrastructure.published;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.agenda.*;
import pl.confitura.jelatyna.agenda.api.InlineAgendaEntry;
import pl.confitura.jelatyna.agenda.api.InlineRoom;
import pl.confitura.jelatyna.agenda.api.InlineTimeSlot;
import pl.confitura.jelatyna.api.model.InlinePresentationWithSpeakers;
import pl.confitura.jelatyna.news.NewsletterApi;
import pl.confitura.jelatyna.page.PageController;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.PublicSpeaker;
import pl.confitura.jelatyna.user.UserController;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/published")
@RequiredArgsConstructor
public class PublishedController {

    private final UserController userController;
    private final PageController pageController;
    private final NewsletterApi newsletterApi;
    private final PresentationRepository presentationRepository;

    // Agenda related (may be null in some profiles)
    private final AgendaService agendaService;
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;

    @GetMapping("/users/search/admins")
    public ResponseEntity<?> admins() {
        return userController.getAdmins();
    }

    @GetMapping("/users/search/speakers")
    public ResponseEntity<?> speakers() {
        return userController.getSpeakers();
    }

    @GetMapping("/users/{id}/public")
    public ResponseEntity<PublicSpeaker> speaker(@PathVariable("id") String id) {
        // Reuse userController endpoint if available; otherwise find from speakers list
        var speakers = userController.getSpeakers().getBody();
        if (speakers == null) {
            return ResponseEntity.notFound().build();
        }
        return speakers.stream()
                .filter(s -> id.equals(s.id()))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/presentations/accepted")
    public List<InlinePresentationWithSpeakers> acceptedPresentations() {
        List<Presentation> accepted = presentationRepository.findAccepted();
        return accepted.stream()
                .filter(p -> !p.isWorkshop())
                .map(InlinePresentationWithSpeakers::new)
                .sorted(Comparator.comparing(InlinePresentationWithSpeakers::title))
                .collect(toList());
    }

    @GetMapping("/workshops/accepted")
    public List<InlinePresentationWithSpeakers> acceptedWorkshops() {
        List<Presentation> accepted = presentationRepository.findAccepted();
        return accepted.stream()
                .filter(Presentation::isWorkshop)
                .map(InlinePresentationWithSpeakers::new)
                .sorted(Comparator.comparing(InlinePresentationWithSpeakers::title))
                .collect(toList());
    }

    @GetMapping("/pages/{page}")
    public ResponseEntity<?> page(@PathVariable("page") String page) {
        return pageController.getPage(page);
    }

    @GetMapping("/news")
    public ResponseEntity<?> news() {
        try {
            return ResponseEntity.ok(DumpedNews.from(newsletterApi.getWebpageNews()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Couldn't fetch news");
        }
    }

    @GetMapping("/agenda/{dayId}")
    public ResponseEntity<?> agenda(@PathVariable String dayId) {
        if (dayRepository == null || timeSlotsRepository == null || roomRepository == null || agendaService == null) {
            return ResponseEntity.notFound().build();
        }
        var dayOpt = dayRepository.findAll().stream().filter(d -> dayId.equals(d.getId())).findFirst();
        if (dayOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var timeSlots = timeSlotsRepository.findByIdDayId(dayId).stream()
                .sorted(Comparator.comparing(pl.confitura.jelatyna.agenda.TimeSlot::getDisplayOrder))
                .map(InlineTimeSlot::from)
                .toList();
        var rooms = roomRepository.findByDayId(dayId).stream()
                .sorted(Comparator.comparing(pl.confitura.jelatyna.agenda.Room::getDisplayOrder))
                .map(InlineRoom::from)
                .toList();
        var entries = agendaService.findByTimeSlotIdDayIdAndMerge(dayId).stream()
                .map(InlineAgendaEntry::from)
                .toList();
        var presentations = presentationRepository.findAccepted()
                .stream()
                .map(InlinePresentationWithSpeakers::new)
                .toList();
        Map<Integer, List<InlineAgendaEntry>> byTimeSlot = entries.stream().collect(groupingBy(InlineAgendaEntry::timeSlotIndex));
        InlineAgenda agenda = new InlineAgenda(timeSlots, rooms, presentations, entries, byTimeSlot);
        return ResponseEntity.ok(agenda);
    }
}
