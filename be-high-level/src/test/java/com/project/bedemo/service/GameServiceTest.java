package com.project.bedemo.service;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class GameServiceTest {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    @Test
    void tryLuckReturnsBoolean() {
        GameService service = new GameService();
        boolean result = service.tryLuck();
        assertThat(result).isInstanceOf(Boolean.class);
    }

    @Test
    void tryLuckCanBeCalledMultipleTimesOnSameDayWithoutException() {
        var fixed = java.time.Clock.fixed(Instant.parse("2025-02-08T12:00:00Z"), ZONE);
        GameService service = new GameService(fixed);
        for (int i = 0; i < 100; i++) {
            service.tryLuck();
        }
    }

    @Test
    void tryLuckDoesNotThrowWhenDayAdvances() {
        var mutableClock = new MutableClock(
                LocalDate.of(2025, 2, 8).atStartOfDay(ZONE).toInstant(),
                ZONE
        );
        GameService service = new GameService(mutableClock);
        service.tryLuck();
        mutableClock.setInstant(LocalDate.of(2025, 2, 9).atStartOfDay(ZONE).toInstant());
        boolean result = service.tryLuck();
        assertThat(result).isInstanceOf(Boolean.class);
    }

    @Test
    void tryLuckWithPercentageZeroAlwaysReturnsFalse() {
        var fixed = java.time.Clock.fixed(Instant.parse("2025-02-08T12:00:00Z"), ZONE);
        GameService service = new GameService(fixed, 0.0, 0.0, 30);
        for (int i = 0; i < 100; i++) {
            boolean result = service.tryLuck();
            assertThat(result).isFalse();
        }
    }

    @Test
    void tryLuckWithPercentageOneAlwaysReturnsTrue() {
        var fixed = java.time.Clock.fixed(Instant.parse("2025-02-08T12:00:00Z"), ZONE);
        GameService service = new GameService(fixed, 1.0, 1.0, 30);
        for (int i = 0; i < 100; i++) {
            boolean result = service.tryLuck();
            assertThat(result).isTrue();
        }
    }
}
