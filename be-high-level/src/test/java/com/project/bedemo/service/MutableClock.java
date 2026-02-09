package com.project.bedemo.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A Clock whose instant can be set, for testing date-boundary behavior.
 */
final class MutableClock extends Clock {

    private volatile Instant instant;
    private final ZoneId zone;

    MutableClock(Instant initial, ZoneId zone) {
        this.instant = initial;
        this.zone = zone;
    }

    void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Override
    public Instant instant() {
        return instant;
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new MutableClock(instant, zone);
    }
}
