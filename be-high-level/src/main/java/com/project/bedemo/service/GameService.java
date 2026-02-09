package com.project.bedemo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GameService {

    private final Clock clock;
    private final AtomicInteger winCount = new AtomicInteger(0);
    private final AtomicReference<LocalDate> lastSeenDate;
    private final double winPercentageBelowThreshold;
    private final double winPercentageAboveThreshold;
    private final int threshold;

    private final Object dateLock = new Object();

    public GameService() {
        this(Clock.systemDefaultZone(), 0.7, 0.4, 30);
    }

    public GameService(
            @Value("${game.win.percentage.below-threshold:0.7}") double winPercentageBelowThreshold,
            @Value("${game.win.percentage.above-threshold:0.4}") double winPercentageAboveThreshold,
            @Value("${game.win.threshold:30}") int threshold) {
        this(Clock.systemDefaultZone(), winPercentageBelowThreshold, winPercentageAboveThreshold, threshold);
    }

    public GameService(Clock clock) {
        this(clock, 0.7, 0.4, 30);
    }

    public GameService(Clock clock, double winPercentageBelowThreshold, double winPercentageAboveThreshold, int threshold) {
        this.clock = clock;
        this.winPercentageBelowThreshold = winPercentageBelowThreshold;
        this.winPercentageAboveThreshold = winPercentageAboveThreshold;
        this.threshold = threshold;
        this.lastSeenDate = new AtomicReference<>(LocalDate.now(clock));
    }

    /**
     * Thread-safe: lock only when calendar day changes (rare); hot path is lock-free.
     */
    public boolean tryLuck() {
        LocalDate today = LocalDate.now(clock);
        if (!lastSeenDate.get().equals(today)) {
            synchronized (dateLock) {
                if (!lastSeenDate.get().equals(today)) {
                    lastSeenDate.set(today);
                    winCount.set(0);
                }
            }
        }

        boolean win = Math.random() < (winCount.get() < threshold ? winPercentageBelowThreshold : winPercentageAboveThreshold);
        if (win) {
            winCount.incrementAndGet();
        }
        return win;
    }
}
