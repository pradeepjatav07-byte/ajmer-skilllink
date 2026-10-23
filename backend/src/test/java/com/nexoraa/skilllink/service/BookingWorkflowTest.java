package com.nexoraa.skilllink.service;

import com.nexoraa.skilllink.domain.BookingStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingWorkflowTest {
    @Test void requestCanBeAcceptedOrCancelledButNotCompletedImmediately() {
        assertTrue(BookingWorkflow.canTransition(BookingStatus.REQUESTED, BookingStatus.ACCEPTED));
        assertTrue(BookingWorkflow.canTransition(BookingStatus.REQUESTED, BookingStatus.CANCELLED));
        assertFalse(BookingWorkflow.canTransition(BookingStatus.REQUESTED, BookingStatus.WORK_COMPLETED));
    }
    @Test void completionRequiresTheProviderProgressionAndCustomerConfirmation() {
        assertTrue(BookingWorkflow.canTransition(BookingStatus.ACCEPTED, BookingStatus.PROVIDER_ON_THE_WAY));
        assertTrue(BookingWorkflow.canTransition(BookingStatus.PROVIDER_ON_THE_WAY, BookingStatus.WORK_STARTED));
        assertTrue(BookingWorkflow.canTransition(BookingStatus.WORK_STARTED, BookingStatus.WORK_COMPLETED));
        assertTrue(BookingWorkflow.canTransition(BookingStatus.WORK_COMPLETED, BookingStatus.CUSTOMER_CONFIRMED));
        assertFalse(BookingWorkflow.canTransition(BookingStatus.CUSTOMER_CONFIRMED, BookingStatus.CANCELLED));
    }
}
