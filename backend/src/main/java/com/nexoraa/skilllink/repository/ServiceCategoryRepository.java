package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, String> {
    List<ServiceCategory> findAllByActiveTrueOrderByNameAsc();
    Optional<ServiceCategory> findBySlugAndActiveTrue(String slug);
    Optional<ServiceCategory> findByNameIgnoreCaseAndActiveTrue(String name);
}
