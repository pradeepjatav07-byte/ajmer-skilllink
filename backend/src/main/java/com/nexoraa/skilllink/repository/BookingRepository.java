package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.Booking;
import com.nexoraa.skilllink.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findAllByOrderByCreatedAtDesc();
    List<Booking> findAllByCustomer_IdOrderByCreatedAtDesc(String customerId);
    List<Booking> findAllByProvider_IdOrderByCreatedAtDesc(String providerId);
    long countByStatus(BookingStatus status);
    long countByEmergencyTrue();
    long countByStatusIn(List<BookingStatus> statuses);
}
