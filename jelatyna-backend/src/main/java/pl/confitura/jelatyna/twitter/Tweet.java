package pl.confitura.jelatyna.twitter;

import lombok.Data;

import java.time.Instant;

@Data
class Tweet {
    private String name;
    private String twitterHandle;
    private String avatar;
    private String text;
    private Instant time;
}