package pl.confitura.jelatyna.conference.registration.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RegistrationConfiguration {

    @Bean
    TicketGenerator ticketGenerator() {
        return new TicketGenerator();
    }

    @Bean
    VoucherService voucherService(VoucherRepository voucherRepository, ParticipationRepository participationRepository) {
        return new VoucherService(voucherRepository, participationRepository);
    }
}
