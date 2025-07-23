package com.aloa.daemon.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Time {
    private static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
}
