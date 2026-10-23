package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, String> {
    List<Complaint> findAllByOrderByCreatedAtDesc();
    long countByStatusIn(List<String> statuses);
}
