package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.domain.*;
import com.nexoraa.skilllink.repository.AttachmentRepository;
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
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final AttachmentRepository attachments;

    @GetMapping
    public List<ApiDtos.BookingView> list(@AuthenticationPrincipal UserEntity actor) { return bookingService.listFor(actor); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    public ApiDtos.BookingView create(@Valid @RequestBody ApiDtos.BookingCreateRequest request,
                                      @AuthenticationPrincipal UserEntity customer) {
        return bookingService.create(request, customer);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ApiDtos.BookingView get(@PathVariable String id, @AuthenticationPrincipal UserEntity actor) {
        return ApiDtos.BookingView.from(bookingService.visibleBooking(id, actor));
    }

    @PostMapping("/{id}/status")
    public ApiDtos.BookingView updateStatus(@PathVariable String id, @Valid @RequestBody ApiDtos.StatusUpdateRequest request,
                                            @AuthenticationPrincipal UserEntity actor) {
        return bookingService.updateStatus(id, request.status(), request.note(), actor);
    }

    @PostMapping("/{id}/work-proof")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public ApiDtos.AttachmentView workProof(@PathVariable String id, @Valid @RequestBody ApiDtos.WorkProofRequest req,
                                            @AuthenticationPrincipal UserEntity actor) {
        Booking booking = bookingService.visibleBooking(id, actor);
        if (actor.getRole() == Role.PROVIDER && (booking.getProvider() == null || !booking.getProvider().getId().equals(actor.getId())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A provider can only add proof to their own booking.");
        String phase = req.phase().toUpperCase();
        if (!List.of("BEFORE", "AFTER").contains(phase)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proof phase must be BEFORE or AFTER.");
        if (!req.demoUrl().startsWith("https://") && !req.demoUrl().startsWith("demo:"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only an approved HTTPS or demo: proof reference is allowed.");
        Attachment attachment = attachments.save(Attachment.builder().booking(booking).uploadedBy(actor).phase(phase)
                .fileName(req.fileName()).demoUrl(req.demoUrl()).capturedAt(req.capturedAt()).consentConfirmed(req.consentConfirmed()).build());
        return new ApiDtos.AttachmentView(attachment.getId(), attachment.getPhase(), attachment.getFileName(), attachment.getDemoUrl(), attachment.getCapturedAt(), actor.getFullName());
    }

    @GetMapping("/{id}/work-proof")
    @Transactional(readOnly = true)
    public List<ApiDtos.AttachmentView> workProof(@PathVariable String id, @AuthenticationPrincipal UserEntity actor) {
        Booking booking = bookingService.visibleBooking(id, actor);
        return attachments.findAllByBooking_IdOrderByCreatedAtAsc(booking.getId()).stream()
                .map(a -> new ApiDtos.AttachmentView(a.getId(), a.getPhase(), a.getFileName(), a.getDemoUrl(), a.getCapturedAt(), a.getUploadedBy().getFullName())).toList();
    }

    @GetMapping("/{id}/share-summary")
    @Transactional(readOnly = true)
    public ApiDtos.ShareSummary shareSummary(@PathVariable String id, @AuthenticationPrincipal UserEntity actor) {
        Booking b = bookingService.visibleBooking(id, actor);
        return new ApiDtos.ShareSummary(b.getBookingCode(), b.getServiceTitle(), b.getCategory().getName(),
                b.getProvider() == null ? "Matching local provider" : b.getProvider().getFullName(),
                b.getAreaName(), b.getAppointmentDate() == null ? b.getPreferredTime() : b.getAppointmentDate() + " · " + b.getPreferredTime(),
                b.getStatus().name(), b.isEmergency(), "Exact address and private contact details are intentionally excluded.");
    }
}
