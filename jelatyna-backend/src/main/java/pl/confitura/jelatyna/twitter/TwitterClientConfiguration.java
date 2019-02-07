package pl.confitura.jelatyna.twitter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.confitura.jelatyna.login.twitter.TwitterConfigurationProperties;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
class TwitterClientConfiguration {

    @Bean
    Twitter twitter(TwitterConfigurationProperties configuration) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(configuration.getApiKey())
                .setOAuthConsumerSecret(configuration.getApiSecret())
                .setOAuthAccessToken(configuration.getAccessToken())
                .setOAuthAccessTokenSecret(configuration.getAccessTokenSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }

    @Bean
    TwitterClient twitterClient(Twitter twitter) {
        return new TwitterClient(twitter);
    }
}