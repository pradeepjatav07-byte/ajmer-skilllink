package com.nexoraa.skilllink.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_booking_customer_created", columnList = "customer_id,created_at"),
        @Index(name = "idx_booking_provider_status", columnList = "provider_id,status"),
        @Index(name = "idx_booking_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.UUID) @Column(length = 36) private String id;
    @Column(name = "booking_code", nullable = false, unique = true, length = 24) private String bookingCode;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "customer_id", nullable = false) private UserEntity customer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "provider_id") private UserEntity provider;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id", nullable = false) private ServiceCategory category;
    @Column(name = "service_title", nullable = false, length = 140) private String serviceTitle;
    @Column(name = "problem_description", nullable = false, length = 1500) private String problemDescription;
    @Column(name = "area_name", nullable = false, length = 80) private String areaName;
    @Column(name = "address_label", nullable = false, length = 220) private String addressLabel;
    @Column(name = "appointment_date") private LocalDate appointmentDate;
    @Column(name = "preferred_time", nullable = false, length = 40) private String preferredTime;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) @Builder.Default private BookingStatus status = BookingStatus.REQUESTED;
    @Column(name = "is_emergency", nullable = false) @Builder.Default private boolean emergency = false;
    @Column(name = "estimated_min") private Integer estimatedMin;
    @Column(name = "estimated_max") private Integer estimatedMax;
    @Column(name = "verified_provider_only", nullable = false) @Builder.Default private boolean verifiedProviderOnly = true;
    @Column(name = "daytime_preferred", nullable = false) @Builder.Default private boolean daytimePreferred = true;
    @Column(name = "female_professional_preferred", nullable = false) @Builder.Default private boolean femaleProfessionalPreferred = false;
    @Column(name = "trusted_contact_name", length = 100) private String trustedContactName;
    @Column(name = "share_with_contact", nullable = false) @Builder.Default private boolean shareWithContact = false;
    @Column(name = "created_at", nullable = false) @Builder.Default private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) @Builder.Default private Instant updatedAt = Instant.now();
    @PreUpdate void touch() { updatedAt = Instant.now(); }
}
