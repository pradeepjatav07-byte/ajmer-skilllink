package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.BookingStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, String> {
    List<BookingStatusHistory> findAllByBooking_IdOrderByCreatedAtAsc(String bookingId);
}
