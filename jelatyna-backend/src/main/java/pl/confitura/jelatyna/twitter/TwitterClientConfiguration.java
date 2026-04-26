package pl.confitura.jelatyna.twitter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.login.twitter.TwitterConfigurationProperties;
import twitter4j.Twitter;

@Configuration
class TwitterClientConfiguration {

    @Bean
    Twitter twitter(TwitterConfigurationProperties configuration) {
        return Twitter.newBuilder()
                .oAuthConsumer(configuration.getApiKey(), configuration.getApiSecret())
                .oAuthAccessToken(configuration.getAccessToken(), configuration.getAccessTokenSecret())
                .build();
    }

    @Bean
    TwitterClient twitterClient(Twitter twitter) {
        return new TwitterClient(twitter);
    }
}