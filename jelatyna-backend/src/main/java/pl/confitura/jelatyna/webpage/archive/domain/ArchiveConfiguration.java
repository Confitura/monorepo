package pl.confitura.jelatyna.webpage.archive.domain;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.confitura.jelatyna.conference.agenda.domain.*;
import pl.confitura.jelatyna.conference.edition.domain.EditionRepository;
import pl.confitura.jelatyna.webpage.faq.domain.FaqEntryRepository;
import pl.confitura.jelatyna.webpage.news.domain.NewsEntryRepository;
import pl.confitura.jelatyna.webpage.partner.domain.PartnerRepository;
import pl.confitura.jelatyna.webpage.page.domain.PageRepository;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import pl.confitura.jelatyna.platform.storage.ResourceConfigurationProperties;
import pl.confitura.jelatyna.user.UserController;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class ArchiveConfiguration {

    private final ObjectMapper objectMapper;
    private final UserController userController;
    private final PageRepository pageRepository;
    private final ResourceConfigurationProperties resourceConfigurationProperties;
    private final EditionRepository editionRepository;
    private final NewsEntryRepository newsEntryRepository;
    private final PresentationRepository presentationRepository;
    private final AgendaService agendaService;
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;
    private final FaqEntryRepository faqEntryRepository;
    private final PartnerRepository partnerRepository;

    @Bean
    WebpageDataDumper webpageDataDumper() {
        return new WebpageDataDumper(
                objectMapper,
                resourceConfigurationProperties.folder(),
                editionRepository,
                userController,
                pageRepository,
                newsEntryRepository,
                presentationRepository,
                agendaService,
                dayRepository,
                timeSlotsRepository,
                roomRepository,
                faqEntryRepository,
                partnerRepository
        );
    }
}
