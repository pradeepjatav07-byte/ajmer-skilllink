package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.domain.Booking;
import com.nexoraa.skilllink.domain.ProviderProfile;
import com.nexoraa.skilllink.domain.ServiceCategory;
import com.nexoraa.skilllink.domain.UserEntity;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}

    public record RegisterRequest(@NotBlank @Size(max=100) String fullName,
                                  @NotBlank @Email @Size(max=190) String email,
                                  @NotBlank @Size(min=10,max=100) String password,
                                  @NotNull PublicRole role,
                                  @Size(max=80) String area) {}
    public enum PublicRole { CUSTOMER, PROVIDER }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record RefreshRequest(@NotBlank String refreshToken) {}
    public record LogoutRequest(@NotBlank String refreshToken) {}
    public record AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresInSeconds, UserView user) {}
    public record UserView(String id, String fullName, String email, String role, String status) {
        public static UserView from(UserEntity u) { return new UserView(u.getId(), u.getFullName(), u.getEmail(), u.getRole().name(), u.getStatus()); }
    }
    public record CategoryView(String id, String name, String slug, String description) {
        public static CategoryView from(ServiceCategory c) { return new CategoryView(c.getId(), c.getName(), c.getSlug(), c.getDescription()); }
    }
    public record ProviderView(String id, String providerCode, String fullName, String category, String area,
                               int experienceYears, String verificationStatus, double ratingAverage,
                               int completedJobs, int trustScore, double onTimeRate, double cancellationRate,
                               boolean emergencyAvailable, boolean femaleProfessional, boolean availableToday,
                               String aboutText, String skillsJson) {
        public static ProviderView from(ProviderProfile p) {
            return new ProviderView(p.getId(), p.getProviderCode(), p.getUser().getFullName(),
                    p.getPrimaryCategory() == null ? null : p.getPrimaryCategory().getName(), p.getPrimaryArea(),
                    p.getExperienceYears(), p.getVerificationStatus(), p.getRatingAverage(), p.getCompletedJobs(),
                    p.getTrustScore(), p.getOnTimeRate(), p.getCancellationRate(), p.isEmergencyAvailable(),
                    p.isFemaleProfessional(), p.isAvailableToday(), p.getAboutText(), p.getSkillsJson());
        }
    }
    public record BookingCreateRequest(@NotBlank @Size(max=140) String serviceTitle,
                                       @NotBlank @Size(max=90) String categorySlug,
                                       @NotBlank @Size(max=80) String areaName,
                                       @NotBlank @Size(max=220) String addressLabel,
                                       @NotBlank @Size(max=1500) String problemDescription,
                                       @NotBlank @Size(max=40) String preferredTime,
                                       LocalDate appointmentDate,
                                       String providerId,
                                       @NotNull Boolean emergency,
                                       Boolean verifiedProviderOnly,
                                       Boolean daytimePreferred,
                                       Boolean femaleProfessionalPreferred,
                                       Boolean shareWithContact,
                                       @Size(max=100) String trustedContactName) {}
    public record StatusUpdateRequest(@NotNull com.nexoraa.skilllink.domain.BookingStatus status, @Size(max=500) String note) {}
    public record BookingView(String id, String bookingCode, String customerName, String providerName,
                              String category, String serviceTitle, String areaName, String preferredTime,
                              LocalDate appointmentDate, String status, boolean emergency,
                              Integer estimatedMin, Integer estimatedMax, Instant createdAt) {
        public static BookingView from(Booking b) {
            return new BookingView(b.getId(), b.getBookingCode(), b.getCustomer().getFullName(),
                    b.getProvider() == null ? null : b.getProvider().getFullName(), b.getCategory().getName(),
                    b.getServiceTitle(), b.getAreaName(), b.getPreferredTime(), b.getAppointmentDate(),
                    b.getStatus().name(), b.isEmergency(), b.getEstimatedMin(), b.getEstimatedMax(), b.getCreatedAt());
        }
    }
    public record FairQuoteResponse(String category, String area, int estimatedMin, int estimatedMax,
                                    int visitFeeMin, int visitFeeMax, int emergencySurchargeMin,
                                    int emergencySurchargeMax, boolean emergency, String disclaimer,
                                    List<String> priceFactors) {}
    public record WorkProofRequest(@NotBlank @Size(max=12) String phase,
                                   @NotBlank @Size(max=500) String fileName,
                                   @NotBlank @Size(max=1000) String demoUrl,
                                   @NotNull Instant capturedAt,
                                   @AssertTrue boolean consentConfirmed) {}
    public record AttachmentView(String id, String phase, String fileName, String demoUrl, Instant capturedAt, String uploadedBy) {}
    public record ShareSummary(String bookingCode, String serviceTitle, String category, String providerName, String areaName, String visitTime, String status, boolean emergency, String privacyNote) {}
    public record RatingRequest(@NotBlank String bookingId, @Min(1) @Max(5) int rating,
                                @Size(max=1200) String reviewText) {}
    public record ReviewView(String id, String bookingCode, String customerName, String providerName,
                             int rating, String reviewText, boolean reported, Instant createdAt) {}
    public record ComplaintRequest(String bookingId, @NotBlank @Size(max=80) String category,
                                   @NotBlank @Size(min=8,max=1200) String description) {}
    public record ComplaintStatusRequest(@NotBlank @Pattern(regexp="OPEN|UNDER_REVIEW|RESOLVED") String status,
                                         @Size(max=1200) String resolutionNote) {}
    public record ComplaintView(String id, String bookingCode, String category, String description,
                                String status, String priority, String customerName, String providerName, Instant createdAt) {}
    public record FairQuoteRuleView(String category, String area, int estimatedMin, int estimatedMax,
                                    int visitFeeMin, int visitFeeMax, int emergencySurchargeMin,
                                    int emergencySurchargeMax, boolean active) {}
    public record FairQuoteRuleUpdateRequest(@NotBlank @Size(max=80) String areaName,
                                             @Min(0) int estimatedMin, @Min(0) int estimatedMax,
                                             @Min(0) int visitFeeMin, @Min(0) int visitFeeMax,
                                             @Min(0) int emergencySurchargeMin, @Min(0) int emergencySurchargeMax) {}
    public record AvailabilityRequest(@NotNull Boolean availableToday, @NotNull Boolean emergencyAvailable) {}
    public record VerificationRequest(@NotBlank @Pattern(regexp="VERIFIED|REJECTED|UNDER_REVIEW") String status,
                                      @Size(max=500) String note) {}
    public record AdminDashboard(long totalCustomers, long totalProviders, long pendingVerifications,
                                 long activeBookings, long emergencyRequests, long openComplaints,
                                 long completedJobs, double averageCustomerRating) {}
}
