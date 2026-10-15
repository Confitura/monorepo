package pl.confitura.jelatyna.chat;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/** Fixed-window per-key rate limiter (requests per rolling minute). */
public class RateLimiter {

    private record Window(long minute, AtomicInteger count) {
    }

    private final int permitsPerMinute;
    private final Clock clock;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicLong lastSweptMinute = new AtomicLong(-1);

    public RateLimiter(int permitsPerMinute, Clock clock) {
        this.permitsPerMinute = permitsPerMinute;
        this.clock = clock;
    }

    /** @return true if a permit was granted, false if the key is over its budget. */
    public boolean tryAcquire(String key) {
        long minute = clock.millis() / 60_000L;
        sweepStaleWindows(minute);
        Window window = windows.compute(key, (k, existing) ->
                (existing == null || existing.minute() != minute)
                        ? new Window(minute, new AtomicInteger(0))
                        : existing);
        return window.count().incrementAndGet() <= permitsPerMinute;
    }

    /** Drops windows from previous minutes once per minute, bounding the map to active callers. */
    private void sweepStaleWindows(long minute) {
        if (lastSweptMinute.getAndSet(minute) != minute) {
            windows.values().removeIf(w -> w.minute() != minute);
        }
    }
}
