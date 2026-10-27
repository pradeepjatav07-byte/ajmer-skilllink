package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.domain.*;
import com.nexoraa.skilllink.repository.RatingsReviewRepository;
import com.nexoraa.skilllink.repository.NotificationRepository;
import com.nexoraa.skilllink.repository.ProviderProfileRepository;
import com.nexoraa.skilllink.service.BookingService;
import com.nexoraa.skilllink.service.ProviderMetricsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class RatingsController {
    private final RatingsReviewRepository reviews;
    private final BookingService bookings;
    private final ProviderProfileRepository providerProfiles;
    private final NotificationRepository notifications;
    private final ProviderMetricsService providerMetricsService;

    @PostMapping("/api/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ApiDtos.ReviewView create(@Valid @RequestBody ApiDtos.RatingRequest req, @AuthenticationPrincipal UserEntity actor) {
        Booking booking = bookings.visibleBooking(req.bookingId(), actor);
        if (!booking.getCustomer().getId().equals(actor.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the booking customer can review this service.");
        if (booking.getStatus() != BookingStatus.CUSTOMER_CONFIRMED) throw new ResponseStatusException(HttpStatus.CONFLICT, "Confirm completion before leaving a rating.");
        if (booking.getProvider() == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "This booking has no assigned provider to review.");
        if (reviews.existsByBooking_Id(booking.getId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "A review has already been submitted for this booking.");
        RatingsReview saved = reviews.save(RatingsReview.builder().booking(booking).customer(actor).provider(booking.getProvider())
                .rating(req.rating()).reviewText(req.reviewText() == null ? null : req.reviewText().trim()).build());
                ProviderProfile profile = providerProfiles.findById(booking.getProvider().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Provider profile was not found."));

        List<RatingsReview> providerReviews =
                reviews.findAllByProvider_IdOrderByCreatedAtDesc(booking.getProvider().getId());
        double averageRating = providerReviews.stream()
                .mapToInt(RatingsReview::getRating)
                .average()
                .orElse(0.0);

        profile.setRatingAverage(Math.round(averageRating * 100.0) / 100.0);
        providerProfiles.save(profile);
        providerMetricsService.recalculateForProvider(booking.getProvider().getId());

        notifications.save(NotificationEntity.builder()
                .user(booking.getProvider())
                .title("New customer review")
                .body(actor.getFullName() + " left a " + saved.getRating()
                        + "-star review for booking " + booking.getBookingCode() + ".")
                .kind("review")
                .build());

        return view(saved);
    }

    @GetMapping("/api/providers/{providerId}/ratings")
    @Transactional(readOnly = true)
    public List<ApiDtos.ReviewView> byProvider(@PathVariable String providerId) {
        return reviews.findAllByProvider_IdOrderByCreatedAtDesc(providerId).stream().map(this::view).toList();
    }

    private ApiDtos.ReviewView view(RatingsReview r) {
        return new ApiDtos.ReviewView(r.getId(), r.getBooking().getBookingCode(), r.getCustomer().getFullName(),
                r.getProvider().getFullName(), r.getRating(), r.getReviewText(), r.isReported(), r.getCreatedAt());
    }
}
