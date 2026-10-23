package com.nexoraa.skilllink.service;

import com.nexoraa.skilllink.api.ApiDtos;
import com.nexoraa.skilllink.domain.*;
import com.nexoraa.skilllink.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookings;
    private final BookingStatusHistoryRepository history;
    private final ProviderProfileRepository providerProfiles;
    private final ServiceCategoryRepository categories;
    private final FairQuoteService fairQuoteService;

    @Transactional
    public ApiDtos.BookingView create(ApiDtos.BookingCreateRequest req, UserEntity customer) {
        ServiceCategory category = categories.findBySlugAndActiveTrue(req.categorySlug())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service category was not found."));
        UserEntity providerUser = null;
        if (req.providerId() != null && !req.providerId().isBlank()) {
            ProviderProfile profile = providerProfiles.findById(req.providerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider was not found."));
            if (!profile.isActive()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Provider is not currently accepting requests.");
            if (Boolean.TRUE.equals(req.emergency()) && (!profile.isEmergencyAvailable() || !profile.isAvailableToday()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This provider is not marked as available for emergency requests.");
            if (Boolean.TRUE.equals(req.verifiedProviderOnly()) && !"VERIFIED".equals(profile.getVerificationStatus()))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This request requires a skill-verified provider.");
            providerUser = profile.getUser();
        } else {
            List<ProviderProfile> candidates = providerProfiles.findAllByActiveTrueOrderByTrustScoreDesc().stream()
                    .filter(p -> p.getPrimaryCategory() != null && p.getPrimaryCategory().getId().equals(category.getId()))
                    .filter(p -> !Boolean.TRUE.equals(req.verifiedProviderOnly()) || "VERIFIED".equals(p.getVerificationStatus()))
                    .filter(p -> !Boolean.TRUE.equals(req.emergency()) || (p.isEmergencyAvailable() && p.isAvailableToday()))
                    .filter(p -> req.areaName() == null || p.getPrimaryArea().equalsIgnoreCase(req.areaName()))
                    .toList();
            if (!candidates.isEmpty()) providerUser = candidates.get(0).getUser();
        }
        ApiDtos.FairQuoteResponse quote = fairQuoteService.quote(category.getSlug(), req.areaName(), Boolean.TRUE.equals(req.emergency()));
        String code = "ASL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        Booking booking = bookings.save(Booking.builder().bookingCode(code).customer(customer).provider(providerUser).category(category)
                .serviceTitle(req.serviceTitle().trim()).problemDescription(req.problemDescription().trim())
                .areaName(req.areaName().trim()).addressLabel(req.addressLabel().trim()).appointmentDate(req.appointmentDate())
                .preferredTime(Boolean.TRUE.equals(req.emergency()) ? "ASAP" : req.preferredTime().trim())
                .emergency(Boolean.TRUE.equals(req.emergency())).status(BookingStatus.REQUESTED)
                .estimatedMin(quote.estimatedMin()).estimatedMax(quote.estimatedMax())
                .verifiedProviderOnly(!Boolean.FALSE.equals(req.verifiedProviderOnly()))
                .daytimePreferred(!Boolean.FALSE.equals(req.daytimePreferred()))
                .femaleProfessionalPreferred(Boolean.TRUE.equals(req.femaleProfessionalPreferred()))
                .shareWithContact(Boolean.TRUE.equals(req.shareWithContact())).trustedContactName(req.trustedContactName()).build());
        history.save(BookingStatusHistory.builder().booking(booking).changedBy(customer).status(BookingStatus.REQUESTED).note("Customer request submitted").build());
        return ApiDtos.BookingView.from(booking);
    }

    @Transactional(readOnly = true)
    public List<ApiDtos.BookingView> listFor(UserEntity user) {
        List<Booking> rows = switch (user.getRole()) {
            case ADMIN -> bookings.findAllByOrderByCreatedAtDesc();
            case PROVIDER -> bookings.findAllByProvider_IdOrderByCreatedAtDesc(user.getId());
            case CUSTOMER -> bookings.findAllByCustomer_IdOrderByCreatedAtDesc(user.getId());
        };
        return rows.stream().map(ApiDtos.BookingView::from).toList();
    }

    @Transactional(readOnly = true)
    public Booking visibleBooking(String id, UserEntity user) {
        Booking b = bookings.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking was not found."));
        boolean visible = user.getRole() == Role.ADMIN || b.getCustomer().getId().equals(user.getId())
                || (b.getProvider() != null && b.getProvider().getId().equals(user.getId()));
        if (!visible) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this booking.");
        return b;
    }

    @Transactional
    public ApiDtos.BookingView updateStatus(String id, BookingStatus next, String note, UserEntity actor) {
        Booking booking = visibleBooking(id, actor);
        boolean admin = actor.getRole() == Role.ADMIN;
        boolean provider = actor.getRole() == Role.PROVIDER;
        boolean customer = actor.getRole() == Role.CUSTOMER;
        if (provider && !SetMembership.sameId(booking.getProvider(), actor)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A provider can only update their own booking.");
        if (customer && !booking.getCustomer().getId().equals(actor.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A customer can only update their own booking.");
        if (customer && !(next == BookingStatus.CUSTOMER_CONFIRMED || next == BookingStatus.CANCELLED || next == BookingStatus.DISPUTED))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customers may confirm, cancel, or dispute their own booking.");
        if (provider && !(next == BookingStatus.ACCEPTED || next == BookingStatus.PROVIDER_ON_THE_WAY || next == BookingStatus.WORK_STARTED || next == BookingStatus.WORK_COMPLETED || next == BookingStatus.CANCELLED))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This status update is not available to providers.");
        if (!admin && !BookingWorkflow.canTransition(booking.getStatus(), next))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This status transition is not allowed.");
        if (customer && next == BookingStatus.CUSTOMER_CONFIRMED && booking.getStatus() != BookingStatus.WORK_COMPLETED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Confirm work only after the provider marks it complete.");
        if (customer && next == BookingStatus.DISPUTED && !Set.of(BookingStatus.WORK_STARTED, BookingStatus.WORK_COMPLETED).contains(booking.getStatus()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A concern can be raised after work has started.");
        booking.setStatus(next);
        bookings.save(booking);
        history.save(BookingStatusHistory.builder().booking(booking).changedBy(actor).status(next).note(note == null ? null : note.trim()).build());
        return ApiDtos.BookingView.from(booking);
    }

    private static class SetMembership {
        static boolean sameId(UserEntity a, UserEntity b) { return a != null && a.getId().equals(b.getId()); }
    }
}
