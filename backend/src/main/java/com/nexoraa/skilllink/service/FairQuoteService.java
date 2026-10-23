package com.nexoraa.skilllink.service;

import com.nexoraa.skilllink.api.ApiDtos;
import com.nexoraa.skilllink.domain.FairQuoteRule;
import com.nexoraa.skilllink.domain.ServiceCategory;
import com.nexoraa.skilllink.repository.FairQuoteRuleRepository;
import com.nexoraa.skilllink.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FairQuoteService {
    private final FairQuoteRuleRepository rules;
    private final ServiceCategoryRepository categories;

    @Transactional(readOnly = true)
    public ApiDtos.FairQuoteResponse quote(String categorySlug, String areaName, boolean emergency) {
        ServiceCategory category = categories.findBySlugAndActiveTrue(categorySlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service category was not found."));
        String area = (areaName == null || areaName.isBlank()) ? "Ajmer - all areas" : areaName.trim();
        FairQuoteRule rule = rules.findFirstByCategory_SlugAndAreaNameAndActiveTrueOrderByUpdatedAtDesc(categorySlug, area)
                .or(() -> rules.findFirstByCategory_SlugAndAreaNameAndActiveTrueOrderByUpdatedAtDesc(categorySlug, "Ajmer - all areas"))
                .orElse(null);
        int low = rule == null ? 350 : rule.getEstimatedMin();
        int high = rule == null ? 900 : rule.getEstimatedMax();
        int visitLow = rule == null ? 80 : rule.getVisitFeeMin();
        int visitHigh = rule == null ? 180 : rule.getVisitFeeMax();
        int emergencyLow = rule == null ? 100 : rule.getEmergencySurchargeMin();
        int emergencyHigh = rule == null ? 250 : rule.getEmergencySurchargeMax();
        return new ApiDtos.FairQuoteResponse(category.getName(), area, low, high, visitLow, visitHigh,
                emergencyLow, emergencyHigh, emergency,
                "Indicative range only. Final price may vary after inspection; confirm the scope and charges with your professional before work begins.",
                List.of("Parts and materials", "Job complexity and access", "Time of visit", "Emergency availability"));
    }
}
