package com.nexoraa.skilllink.service;

import com.nexoraa.skilllink.domain.Booking;
import com.nexoraa.skilllink.domain.BookingStatus;
import com.nexoraa.skilllink.domain.BookingStatusHistory;
import com.nexoraa.skilllink.domain.ProviderProfile;
import com.nexoraa.skilllink.repository.BookingRepository;
import com.nexoraa.skilllink.repository.BookingStatusHistoryRepository;
import com.nexoraa.skilllink.repository.ProviderProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProviderMetricsService {
    private static final ZoneId AJMER_ZONE = ZoneId.of("Asia/Kolkata");
    private static final Duration ON_TIME_WINDOW = Duration.ofMinutes(15);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private final BookingRepository bookings;
    private final BookingStatusHistoryRepository history;
    private final ProviderProfileRepository profiles;

    @Transactional
    public void recalculateForProvider(String providerId) {
        if (providerId == null || providerId.isBlank()) {
            return;
        }

        ProviderProfile profile = profiles.findById(providerId).orElse(null);
        if (profile == null) {
            return;
        }

        List<Booking> providerBookings =
                bookings.findAllByProvider_IdOrderByCreatedAtDesc(providerId);

        int completedJobs = (int) providerBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CUSTOMER_CONFIRMED)
                .count();

        int onTimeSampleCount = 0;
        int onTimeVisits = 0;

        for (Booking booking : providerBookings) {
            if (booking.getStatus() != BookingStatus.CUSTOMER_CONFIRMED
                    || booking.getAppointmentDate() == null
                    || booking.getPreferredTime() == null
                    || booking.getPreferredTime().isBlank()) {
                continue;
            }

            LocalTime scheduledTime;
            try {
                scheduledTime = LocalTime.parse(
                        booking.getPreferredTime().trim(), TIME_FORMAT);
            } catch (DateTimeParseException ex) {
                continue;
            }

            BookingStatusHistory workStarted = history
                    .findAllByBooking_IdOrderByCreatedAtAsc(booking.getId())
                    .stream()
                    .filter(event -> event.getStatus() == BookingStatus.WORK_STARTED)
                    .findFirst()
                    .orElse(null);

            if (workStarted == null || workStarted.getCreatedAt() == null) {
                continue;
            }

            Instant scheduledStart = LocalDateTime
                    .of(booking.getAppointmentDate(), scheduledTime)
                    .atZone(AJMER_ZONE)
                    .toInstant();

            Duration difference = Duration.between(
                    scheduledStart, workStarted.getCreatedAt()).abs();

            onTimeSampleCount++;
            if (difference.compareTo(ON_TIME_WINDOW) <= 0) {
                onTimeVisits++;
            }
        }

        double onTimeRate = onTimeSampleCount == 0
                ? 0.0
                : (100.0 * onTimeVisits) / onTimeSampleCount;

        profile.setCompletedJobs(completedJobs);
        profile.setOnTimeSampleCount(onTimeSampleCount);
        profile.setOnTimeRate(Math.round(onTimeRate * 10.0) / 10.0);

        double ratingScore = Math.max(0.0,
                Math.min(5.0, profile.getRatingAverage())) * 20.0;
        double jobScore = Math.min(completedJobs, 10) * 10.0;

        int trustScore = (int) Math.round(
                (ratingScore * 0.40)
                        + (jobScore * 0.30)
                        + (profile.getOnTimeRate() * 0.30));

        profile.setTrustScore(Math.max(0, Math.min(100, trustScore)));
        profiles.save(profile);
    }
}