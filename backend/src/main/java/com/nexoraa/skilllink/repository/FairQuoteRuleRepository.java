package com.nexoraa.skilllink.repository;

import com.nexoraa.skilllink.domain.FairQuoteRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FairQuoteRuleRepository extends JpaRepository<FairQuoteRule, String> {
    Optional<FairQuoteRule> findFirstByCategory_SlugAndAreaNameAndActiveTrueOrderByUpdatedAtDesc(String slug, String areaName);
    List<FairQuoteRule> findAllByActiveTrueOrderByCategory_NameAsc();
}
