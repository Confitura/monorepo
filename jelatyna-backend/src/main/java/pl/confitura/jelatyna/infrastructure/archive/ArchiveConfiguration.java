package pl.confitura.jelatyna.infrastructure.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.confitura.jelatyna.news.NewsletterApi;
import pl.confitura.jelatyna.page.PageController;
import pl.confitura.jelatyna.presentation.PresentationRepository;
import pl.confitura.jelatyna.resource.ResourceConfigurationProperties;
import pl.confitura.jelatyna.user.UserController;
import pl.confitura.jelatyna.agenda.AgendaRepository;
import pl.confitura.jelatyna.agenda.DayRepository;
import pl.confitura.jelatyna.agenda.TimeSlotsRepository;
import pl.confitura.jelatyna.agenda.RoomRepository;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class ArchiveConfiguration {

    private final ObjectMapper objectMapper;
    private final UserController userController;
    private final PageController pageController;
    private final ResourceConfigurationProperties resourceConfigurationProperties;
    private final NewsletterApi newsletterApi;
    private final PresentationRepository presentationRepository;
    private final AgendaRepository agendaRepository;
    private final DayRepository dayRepository;
    private final TimeSlotsRepository timeSlotsRepository;
    private final RoomRepository roomRepository;

    @Bean
    WebpageDataDumper webpageDataDumper() {
        return new WebpageDataDumper(
                objectMapper,
                resourceConfigurationProperties.folder() + "/edition-2025",
                userController,
                pageController,
                newsletterApi,
                presentationRepository,
                agendaRepository,
                dayRepository,
                timeSlotsRepository,
                roomRepository
        );
    }
}
