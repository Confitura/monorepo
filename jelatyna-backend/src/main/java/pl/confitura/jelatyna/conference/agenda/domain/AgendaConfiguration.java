package pl.confitura.jelatyna.conference.agenda.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;

@Configuration
class AgendaConfiguration {

    @Bean
    AgendaService agendaService(TimeSlotsRepository timeSlotsRepository, RoomRepository roomRepository,
                                PresentationRepository presentationRepository, AgendaRepository agendaRepository,
                                DayRepository dayRepository) {
        return new AgendaService(timeSlotsRepository, roomRepository, presentationRepository, agendaRepository, dayRepository);
    }

    @Bean
    IcalExportService icalExportService(AgendaService agendaService, DayRepository dayRepository,
                                        AgendaRepository agendaRepository) {
        return new IcalExportService(agendaService, dayRepository, agendaRepository);
    }
}
