package com.nexoraa.skilllink.service;

import com.nexoraa.skilllink.domain.BookingStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingWorkflowEdgeCaseTest {
    @Test
    void cancelledAndConfirmedBookingsAreTerminal() {
        assertAll(
            () -> assertFalse(BookingWorkflow.canTransition(BookingStatus.CANCELLED, BookingStatus.REQUESTED)),
            () -> assertFalse(BookingWorkflow.canTransition(BookingStatus.CANCELLED, BookingStatus.ACCEPTED)),
            () -> assertFalse(BookingWorkflow.canTransition(BookingStatus.CUSTOMER_CONFIRMED, BookingStatus.DISPUTED)),
            () -> assertTrue(BookingWorkflow.canTransition(BookingStatus.CANCELLED, BookingStatus.CANCELLED))
        );
    }

    @Test
    void disputedBookingsCanOnlyBeConfirmedOrCancelled() {
        assertAll(
            () -> assertTrue(BookingWorkflow.canTransition(BookingStatus.DISPUTED, BookingStatus.CUSTOMER_CONFIRMED)),
            () -> assertTrue(BookingWorkflow.canTransition(BookingStatus.DISPUTED, BookingStatus.CANCELLED)),
            () -> assertFalse(BookingWorkflow.canTransition(BookingStatus.DISPUTED, BookingStatus.WORK_STARTED)),
            () -> assertFalse(BookingWorkflow.canTransition(BookingStatus.DISPUTED, BookingStatus.ACCEPTED))
        );
    }
}
