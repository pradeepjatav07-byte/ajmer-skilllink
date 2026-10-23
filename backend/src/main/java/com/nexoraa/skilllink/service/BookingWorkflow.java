package com.nexoraa.skilllink.service;

import com.nexoraa.skilllink.domain.BookingStatus;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class BookingWorkflow {
    private BookingWorkflow() {}
    private static final Map<BookingStatus, Set<BookingStatus>> ALLOWED = Map.of(
            BookingStatus.REQUESTED, EnumSet.of(BookingStatus.ACCEPTED, BookingStatus.CANCELLED),
            BookingStatus.ACCEPTED, EnumSet.of(BookingStatus.PROVIDER_ON_THE_WAY, BookingStatus.CANCELLED),
            BookingStatus.PROVIDER_ON_THE_WAY, EnumSet.of(BookingStatus.WORK_STARTED, BookingStatus.CANCELLED),
            BookingStatus.WORK_STARTED, EnumSet.of(BookingStatus.WORK_COMPLETED, BookingStatus.DISPUTED),
            BookingStatus.WORK_COMPLETED, EnumSet.of(BookingStatus.CUSTOMER_CONFIRMED, BookingStatus.DISPUTED),
            BookingStatus.CUSTOMER_CONFIRMED, EnumSet.noneOf(BookingStatus.class),
            BookingStatus.CANCELLED, EnumSet.noneOf(BookingStatus.class),
            BookingStatus.DISPUTED, EnumSet.of(BookingStatus.CUSTOMER_CONFIRMED, BookingStatus.CANCELLED)
    );
    public static boolean canTransition(BookingStatus from, BookingStatus to) {
        return from == to || ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }
}
