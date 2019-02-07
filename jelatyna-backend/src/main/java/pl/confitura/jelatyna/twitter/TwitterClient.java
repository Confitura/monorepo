package pl.confitura.jelatyna.twitter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class TwitterClient {
    private final Twitter twitter;

    List<Tweet> getTweets() {
        try {
            ResponseList<Status> timelines = twitter.getUserTimeline("confiturapl");
            return timelines.stream().map(this::mapTweet).collect(Collectors.toList());
        } catch (TwitterException e) {
            log.warn("Couldn't load tweets", e);
            return Collections.emptyList();
        }
    }

    private Tweet mapTweet(Status status) {
        Tweet tweet = new Tweet();
        tweet.setText(status.getText());
        tweet.setName(status.getUser().getName());
        tweet.setTwitterHandle(status.getUser().getScreenName());
        tweet.setAvatar(status.getUser().get400x400ProfileImageURLHttps());
        tweet.setTime(status.getCreatedAt().toInstant());
        return tweet;
    }
}
