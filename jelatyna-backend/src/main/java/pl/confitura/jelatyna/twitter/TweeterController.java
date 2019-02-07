package pl.confitura.jelatyna.twitter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tweets")
class TweeterController {

    private final TwitterClient twitterClient;

    @GetMapping
    List<Tweet> getTweets() {
        return twitterClient.getTweets();
    }

}
