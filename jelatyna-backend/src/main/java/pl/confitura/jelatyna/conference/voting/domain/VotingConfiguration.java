package pl.confitura.jelatyna.conference.voting.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class VotingConfiguration {

    @Bean
    VoterIpsCleaner voterIpsCleaner(VoteRepository voteRepository) {
        return new VoterIpsCleaner(voteRepository);
    }
}
