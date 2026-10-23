package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.domain.*;
import com.nexoraa.skilllink.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository users;
    private final ProviderProfileRepository providers;
    private final BookingRepository bookings;
    private final ComplaintRepository complaints;
    private final RatingsReviewRepository reviews;
    private final ServiceCategoryRepository categories;
    private final FairQuoteRuleRepository quoteRules;
    private final AuditLogRepository auditLogs;

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public ApiDtos.AdminDashboard dashboard() {
        double average = reviews.findAll().stream().mapToInt(RatingsReview::getRating).average().orElse(0.0);
        return new ApiDtos.AdminDashboard(users.countByRole(Role.CUSTOMER), users.countByRole(Role.PROVIDER),
                providers.countByVerificationStatus("UNDER_REVIEW"),
                bookings.countByStatusIn(List.of(BookingStatus.REQUESTED, BookingStatus.ACCEPTED, BookingStatus.PROVIDER_ON_THE_WAY, BookingStatus.WORK_STARTED)),
                bookings.countByEmergencyTrue(), complaints.countByStatusIn(List.of("OPEN", "UNDER_REVIEW")),
                bookings.countByStatus(BookingStatus.CUSTOMER_CONFIRMED), Math.round(average * 100.0) / 100.0);
    }

    @GetMapping("/providers/verification")
    @Transactional(readOnly = true)
    public List<ApiDtos.ProviderView> verificationQueue(@RequestParam(defaultValue = "UNDER_REVIEW") String status) {
        return providers.findAllByVerificationStatusOrderByCreatedAtAsc(status.toUpperCase()).stream().map(ApiDtos.ProviderView::from).toList();
    }

    @PatchMapping("/providers/{id}/verification")
    @Transactional
    public ApiDtos.ProviderView verifyProvider(@PathVariable String id, @Valid @RequestBody ApiDtos.VerificationRequest request,
                                               @org.springframework.security.core.annotation.AuthenticationPrincipal UserEntity actor) {
        ProviderProfile p = providers.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider profile was not found."));
        p.setVerificationStatus(request.status());
        providers.save(p);
        auditLogs.save(AuditLog.builder().actor(actor).actionKey("PROVIDER_" + request.status())
                .targetType("PROVIDER_PROFILE").targetId(id)
                .details((request.note() == null ? "" : request.note().trim()) + " Status: " + request.status()).build());
        return ApiDtos.ProviderView.from(p);
    }

    @GetMapping("/categories")
    @Transactional(readOnly = true)
    public List<ApiDtos.CategoryView> categories() { return categories.findAllByActiveTrueOrderByNameAsc().stream().map(ApiDtos.CategoryView::from).toList(); }

    @GetMapping("/fair-quote-rules")
    @Transactional(readOnly = true)
    public List<ApiDtos.FairQuoteRuleView> fairQuoteRules() {
        return quoteRules.findAllByActiveTrueOrderByCategory_NameAsc().stream().map(r -> new ApiDtos.FairQuoteRuleView(
                r.getCategory().getName(), r.getAreaName(), r.getEstimatedMin(), r.getEstimatedMax(), r.getVisitFeeMin(),
                r.getVisitFeeMax(), r.getEmergencySurchargeMin(), r.getEmergencySurchargeMax(), r.isActive())).toList();
    }

    @PutMapping("/fair-quote-rules/{categorySlug}")
    @Transactional
    public ApiDtos.FairQuoteRuleView updateFairQuoteRule(@PathVariable String categorySlug,
                                                          @Valid @RequestBody ApiDtos.FairQuoteRuleUpdateRequest req) {
        if (req.estimatedMax() < req.estimatedMin() || req.visitFeeMax() < req.visitFeeMin() || req.emergencySurchargeMax() < req.emergencySurchargeMin())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each maximum must be greater than or equal to its minimum.");
        ServiceCategory category = categories.findBySlugAndActiveTrue(categorySlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service category was not found."));
        FairQuoteRule rule = quoteRules.findFirstByCategory_SlugAndAreaNameAndActiveTrueOrderByUpdatedAtDesc(categorySlug, req.areaName())
                .orElseGet(() -> FairQuoteRule.builder().category(category).areaName(req.areaName()).build());
        rule.setEstimatedMin(req.estimatedMin()); rule.setEstimatedMax(req.estimatedMax());
        rule.setVisitFeeMin(req.visitFeeMin()); rule.setVisitFeeMax(req.visitFeeMax());
        rule.setEmergencySurchargeMin(req.emergencySurchargeMin()); rule.setEmergencySurchargeMax(req.emergencySurchargeMax());
        rule.setActive(true);
        FairQuoteRule saved = quoteRules.save(rule);
        return new ApiDtos.FairQuoteRuleView(category.getName(), saved.getAreaName(), saved.getEstimatedMin(), saved.getEstimatedMax(),
                saved.getVisitFeeMin(), saved.getVisitFeeMax(), saved.getEmergencySurchargeMin(), saved.getEmergencySurchargeMax(), saved.isActive());
    }

    @GetMapping("/analytics")
    @Transactional(readOnly = true)
    public Map<String,Object> analytics() {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("bookingStatusCounts", Map.of(
                "requested", bookings.countByStatus(BookingStatus.REQUESTED),
                "accepted", bookings.countByStatus(BookingStatus.ACCEPTED),
                "providerOnTheWay", bookings.countByStatus(BookingStatus.PROVIDER_ON_THE_WAY),
                "workStarted", bookings.countByStatus(BookingStatus.WORK_STARTED),
                "workCompleted", bookings.countByStatus(BookingStatus.WORK_COMPLETED),
                "customerConfirmed", bookings.countByStatus(BookingStatus.CUSTOMER_CONFIRMED)));
        result.put("providerVerification", Map.of("underReview", providers.countByVerificationStatus("UNDER_REVIEW"),
                "verified", providers.countByVerificationStatus("VERIFIED"), "rejected", providers.countByVerificationStatus("REJECTED")));
        result.put("reportedReviews", reviews.countByReportedTrue());
        result.put("dataNotice", "Illustrative demo analytics; all seeded records are fictional.");
        return result;
    }

    @GetMapping("/audit-logs")
    @Transactional(readOnly = true)
    public List<AuditView> auditLogs() {
        return auditLogs.findTop100ByOrderByCreatedAtDesc().stream().map(a -> new AuditView(a.getId(),
                a.getActor() == null ? "System" : a.getActor().getFullName(), a.getActionKey(), a.getTargetType(),
                a.getTargetId(), a.getDetails(), a.getCreatedAt())).toList();
    }

    @GetMapping("/complaints")
    @Transactional(readOnly = true)
    public List<ApiDtos.ComplaintView> complaints() {
        return complaints.findAllByOrderByCreatedAtDesc().stream().map(c -> new ApiDtos.ComplaintView(c.getId(),
                c.getBooking() == null ? null : c.getBooking().getBookingCode(), c.getCategory(), c.getDescription(), c.getStatus(),
                c.getPriority(), c.getCustomer().getFullName(), c.getProvider() == null ? null : c.getProvider().getFullName(), c.getCreatedAt())).toList();
    }

    public record AuditView(String id, String actor, String action, String targetType, String targetId, String details, java.time.Instant createdAt) {}
}
