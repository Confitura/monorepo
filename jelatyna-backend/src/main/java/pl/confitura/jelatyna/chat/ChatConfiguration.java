package pl.confitura.jelatyna.chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ChatConfiguration {

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
