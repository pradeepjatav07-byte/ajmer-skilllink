package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    List<Attachment> findAllByBooking_IdOrderByCreatedAtAsc(String bookingId);
}
