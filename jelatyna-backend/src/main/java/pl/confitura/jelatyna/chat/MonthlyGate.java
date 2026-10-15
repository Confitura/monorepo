package pl.confitura.jelatyna.chat;

import java.time.Clock;
import java.time.YearMonth;
import java.time.ZoneOffset;

/**
 * Global monthly cap on paid Datalinks calls — an automatic guard against runaway cost.
 * MVP: an in-memory call counter that resets on restart (not durable spend tracking).
 * The manual kill-switch is {@code chat.enabled=false}.
 */
public class MonthlyGate {

    private final int cap;
    private final Clock clock;
    private YearMonth month;
    private int count;

    public MonthlyGate(int cap, Clock clock) {
        this.cap = cap;
        this.clock = clock;
        this.month = currentMonth();
    }

    private YearMonth currentMonth() {
        return YearMonth.now(clock.withZone(ZoneOffset.UTC));
    }

    private synchronized void rollIfNeeded() {
        YearMonth now = currentMonth();
        if (!now.equals(month)) {
            month = now;
            count = 0;
        }
    }

    /** @return true if the monthly cap has already been reached. */
    public synchronized boolean isExhausted() {
        rollIfNeeded();
        return count >= cap;
    }

    /** Records one paid call against the monthly budget. */
    public synchronized void consume() {
        rollIfNeeded();
        count++;
    }
}
