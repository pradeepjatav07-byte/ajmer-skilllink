package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.domain.*;
import com.nexoraa.skilllink.repository.ComplaintRepository;
import com.nexoraa.skilllink.service.BookingService;
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
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintsController {
    private final ComplaintRepository complaints;
    private final BookingService bookings;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ApiDtos.ComplaintView create(@Valid @RequestBody ApiDtos.ComplaintRequest req, @AuthenticationPrincipal UserEntity actor) {
        Booking booking = null;
        UserEntity provider = null;
        if (req.bookingId() != null && !req.bookingId().isBlank()) {
            booking = bookings.visibleBooking(req.bookingId(), actor);
            if (!booking.getCustomer().getId().equals(actor.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the customer can open a complaint for this booking.");
            provider = booking.getProvider();
        }
        Complaint saved = complaints.save(Complaint.builder().booking(booking).customer(actor).provider(provider)
                .category(req.category().trim()).description(req.description().trim()).status("OPEN").priority("NORMAL").build());
        return view(saved);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ApiDtos.ComplaintView> list(@AuthenticationPrincipal UserEntity actor) {
        List<Complaint> rows = complaints.findAllByOrderByCreatedAtDesc();
        if (actor.getRole() == Role.ADMIN) return rows.stream().map(this::view).toList();
        return rows.stream().filter(c -> actor.getId().equals(c.getCustomer().getId()) ||
                (c.getProvider() != null && actor.getId().equals(c.getProvider().getId()))).map(this::view).toList();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ApiDtos.ComplaintView updateStatus(@PathVariable String id, @Valid @RequestBody ApiDtos.ComplaintStatusRequest req) {
        Complaint c = complaints.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint was not found."));
        c.setStatus(req.status()); c.setResolutionNote(req.resolutionNote());
        return view(complaints.save(c));
    }

    private ApiDtos.ComplaintView view(Complaint c) {
        return new ApiDtos.ComplaintView(c.getId(), c.getBooking() == null ? null : c.getBooking().getBookingCode(),
                c.getCategory(), c.getDescription(), c.getStatus(), c.getPriority(), c.getCustomer().getFullName(),
                c.getProvider() == null ? null : c.getProvider().getFullName(), c.getCreatedAt());
    }
}
