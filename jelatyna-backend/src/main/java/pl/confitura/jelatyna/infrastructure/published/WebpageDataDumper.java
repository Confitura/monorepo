package pl.confitura.jelatyna.infrastructure.published;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.agenda.*;
import pl.confitura.jelatyna.agenda.api.InlineAgendaEntry;
import pl.confitura.jelatyna.agenda.api.InlineRoom;
import pl.confitura.jelatyna.agenda.api.InlineTimeSlot;
import pl.confitura.jelatyna.api.model.InlinePresentationWithSpeakers;
import pl.confitura.jelatyna.edition.Edition;
import pl.confitura.jelatyna.edition.EditionRepository;
import pl.confitura.jelatyna.faq.FaqEntryDto;
import pl.confitura.jelatyna.faq.FaqEntryRepository;
import pl.confitura.jelatyna.news.NewsEntryRepository;
import pl.confitura.jelatyna.partner.PartnerDto;
import pl.confitura.jelatyna.partner.PartnerRepository;
import pl.confitura.jelatyna.page.PageController;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.user.PublicSpeaker;
import pl.confitura.jelatyna.user.UserController;
import pl.confitura.jelatyna.user.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class WebpageDataDumper {

    private final ObjectMapper objectMapper;
    private final String resourcesFolder;
    private final EditionRepository editionRepository;

    private final UserController userController;
    private final UserRepository userRepository;
    private final PageController pageController;
    private final NewsEntryRepository newsEntryRepository;
    private final PresentationRepository presentationRepository;

    // Agenda-related repositories
    private final AgendaService agendaService;
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;
    private final FaqEntryRepository faqEntryRepository;
    private final PartnerRepository partnerRepository;

    private final AtomicReference<Instant> lastDumpAt = new AtomicReference<>();

    @Scheduled(fixedRate = 600000)
    @Transactional
    public void dumpAll() {
        Edition edition = editionRepository.findCurrent();
        String folder = resourcesFolder + "/edition-" + edition.getYear();
        dumpEdition(edition, folder);
        dumpAdmins(folder);
        dumpVolunteers(folder);
        dumpSpeakers(edition, folder);
        dumpAcceptedPresentations(edition, folder);
        dumpAcceptedWorkshops(edition, folder);
        dumpPages(folder);
        dumpNews(folder);
        dumpAgendaByDay(edition, folder);
        dumpFaqEntries(folder);
        dumpPartners(folder);
        lastDumpAt.set(Instant.now());
    }

    public Instant getLastDumpAt() {
        return lastDumpAt.get();
    }

    void dumpEdition(Edition edition, String folder) {
        dumbData(folder, DumpedEdition.from(edition), "edition.json");
    }

    void dumpFaqEntries(String folder) {
        var entries = faqEntryRepository.findPublishedOrdered().stream()
                .map(FaqEntryDto::from)
                .toList();
        dumbData(folder, entries, "/faq/entries.json");
    }

    void dumpPartners(String folder) {
        var partners = partnerRepository.findPublished().stream()
                .map(PartnerDto::from)
                .toList();
        dumbData(folder, partners, "/partners/list.json");
    }

    void dumpPages(String folder) {
        for (String page : pageController.getPages()) {
            var content = pageController.getPage(page);
            dumbData(folder, content.getBody(), "/pages/" + page + ".json");
        }
    }

    public void dumpAdmins(String folder) {
        var users = userController.getAdmins().getBody();
        dumbData(folder, users, "/users/search/admins.json");
    }

    public void dumpVolunteers(String folder) {
        var users = userController.getVolunteers().getBody();
        dumbData(folder, users, "/users/search/volunteers.json");
    }

    @Transactional
    public void dumpSpeakers(Edition edition, String folder) {
        var speakers = userRepository.findAllAcceptedIn(edition).stream()
                .map(PublicSpeaker::from)
                .collect(toSet());
        dumpEachSpeaker(speakers, folder);
        dumbData(folder, speakers, "/users/search/speakers.json");
    }

    public void dumpEachSpeaker(Collection<PublicSpeaker> speakers, String folder) {
        if (speakers == null) {
            return;
        }
        for (PublicSpeaker speaker : speakers) {
            dumbData(folder, speaker, "/users/" + speaker.id() + "/public.json");
        }
    }

    public void dumpAcceptedPresentations(Edition edition, String folder) {
        List<Presentation> accepted = presentationRepository.findAcceptedIn(edition);
        List<InlinePresentationWithSpeakers> presentations = accepted.stream()
                .filter(p -> !p.isWorkshop())
                .map(InlinePresentationWithSpeakers::new)
                .sorted(Comparator.comparing(InlinePresentationWithSpeakers::sortableTitle))
                .collect(toList());
        dumbData(folder, presentations, "/presentations/accepted.json");
    }

    public void dumpAcceptedWorkshops(Edition edition, String folder) {
        List<Presentation> accepted = presentationRepository.findAcceptedIn(edition);
        List<InlinePresentationWithSpeakers> workshops = accepted.stream()
                .filter(Presentation::isWorkshop)
                .map(InlinePresentationWithSpeakers::new)
                .sorted(Comparator.comparing(InlinePresentationWithSpeakers::sortableTitle))
                .collect(toList());
        dumbData(folder, workshops, "/workshops/accepted.json");
    }

    private void dumpNews(String folder) {
        dumbData(folder, DumpedNews.from(newsEntryRepository.findPublicAt(Instant.now())), "news.json");
    }

    public void dumpAgendaByDay(Edition edition, String folder) {
        if (dayRepository == null || timeSlotsRepository == null || roomRepository == null || agendaService == null) {
            // Agenda repositories not wired (e.g., in tests using legacy constructor) - skip dumping agenda.
            return;
        }
        var days = dayRepository.findAllIn(edition);
        if (days == null || days.isEmpty()) {
            return;
        }
        for (var day : days) {
            String dayId = day.getId();
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

            var presentations = presentationRepository.findAcceptedIn(edition)
                    .stream()
                    .map(InlinePresentationWithSpeakers::new)
                    .toList();

            Map<Integer, List<InlineAgendaEntry>> byTimeSlot = entries.stream().collect(groupingBy(InlineAgendaEntry::timeSlotIndex));
            InlineAgenda agenda = new InlineAgenda(day.getDate(), timeSlots, rooms, presentations, entries, byTimeSlot);
            dumbData(folder, agenda, "/agenda/" + dayId + ".json");
        }
    }

    @SneakyThrows
    private void dumbData(String folder, Object data, String targetPath) {
        String json = objectMapper.writeValueAsString(data);
        dumpData(folder, json, targetPath);
    }


    @SneakyThrows
    private void dumpData(String folder, String json, String targetPath) {
        Path filePath = Path.of(folder, targetPath);
        if (Files.notExists(filePath)) {
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
        }
        Files.writeString(filePath, json);
    }
}
