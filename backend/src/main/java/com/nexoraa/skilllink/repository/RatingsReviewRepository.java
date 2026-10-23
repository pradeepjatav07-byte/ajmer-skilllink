package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.RatingsReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingsReviewRepository extends JpaRepository<RatingsReview, String> {
    List<RatingsReview> findAllByProvider_IdOrderByCreatedAtDesc(String providerId);
    boolean existsByBooking_Id(String bookingId);
    long countByReportedTrue();
}
