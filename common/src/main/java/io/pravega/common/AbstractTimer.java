/**
 * Copyright (c) 2017 Dell Inc., or its subsidiaries. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.pravega.common;

import java.time.Duration;

/**
 * Provides a customizable way of measuring elapsed time.
 */
public abstract class AbstractTimer {
    protected static final int NANOS_TO_MILLIS = 1000 * 1000;

    /**
     * Gets the elapsed time, in nanoseconds.
     *
     * @return Long indicating elapsed time, in nanoseconds.
     */
    public abstract long getElapsedNanos();

    /**
     * Gets the elapsed time, in milliseconds.
     *
     *  @return Long indicating elapsed time, in milliseconds.
     */
    public long getElapsedMillis() {
        return getElapsedNanos() / NANOS_TO_MILLIS;
    }

    /**
     * Gets the elapsed time.
     *
     * @return Duration indicating elapsed time.
     */
    public Duration getElapsed() {
        return Duration.ofNanos(getElapsedNanos());
    }

    @Override
    public String toString() {
        return getElapsedNanos() + "us";
    }
}
