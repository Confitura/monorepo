package pl.confitura.jelatyna.webpage.chat.domain;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.conference.presentation.domain.PresentationRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(ChatConfigurationProperties.class)
public class ChatConfiguration {

    @Bean
    ChatService chatService(ChatConfigurationProperties properties, DatalinksClient datalinksClient,
                            SpeakerDirectory speakerDirectory, RateLimiter chatRateLimiter,
                            MonthlyGate chatMonthlyGate, AnswerCache chatAnswerCache, ObjectMapper objectMapper) {
        return new ChatService(properties, datalinksClient, speakerDirectory, chatRateLimiter, chatMonthlyGate,
                chatAnswerCache, objectMapper);
    }

    @Bean
    AcceptedSpeakerDirectory acceptedSpeakerDirectory(PresentationRepository presentationRepository) {
        return new AcceptedSpeakerDirectory(presentationRepository);
    }

    @Bean
    Clock chatClock() {
        return Clock.systemUTC();
    }

    @Bean(destroyMethod = "shutdown")
    ExecutorService chatExecutor() {
        return Executors.newFixedThreadPool(8);
    }

    @Bean
    RateLimiter chatRateLimiter(ChatConfigurationProperties properties, Clock chatClock) {
        return new RateLimiter(properties.getRateLimitPerMinute(), chatClock);
    }

    @Bean
    MonthlyGate chatMonthlyGate(ChatConfigurationProperties properties, Clock chatClock) {
        return new MonthlyGate(properties.getMonthlyCallCap(), chatClock);
    }

    @Bean
    AnswerCache chatAnswerCache() {
        return new AnswerCache(500);
    }
}
