package pl.confitura.jelatyna.chat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Small bounded LRU cache of final answers, keyed by the normalized question. */
public class AnswerCache {

    private final Map<String, String> cache;

    public AnswerCache(int maxEntries) {
        this.cache = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > maxEntries;
            }
        });
    }

    private static String key(String question) {
        return question == null ? "" : question.trim().toLowerCase();
    }

    public Optional<String> get(String question) {
        return Optional.ofNullable(cache.get(key(question)));
    }

    public void put(String question, String answer) {
        cache.put(key(question), answer);
    }
}
