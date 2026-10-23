package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.domain.ProviderProfile;
import com.nexoraa.skilllink.domain.UserEntity;
import com.nexoraa.skilllink.repository.ProviderProfileRepository;
import com.nexoraa.skilllink.repository.ServiceCategoryRepository;
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
public class DiscoveryController {
    private final ServiceCategoryRepository categories;
    private final ProviderProfileRepository providers;

    @GetMapping("/api/services")
    @Transactional(readOnly = true)
    public List<ApiDtos.CategoryView> services() {
        return categories.findAllByActiveTrueOrderByNameAsc().stream().map(ApiDtos.CategoryView::from).toList();
    }

    @GetMapping("/api/providers")
    @Transactional(readOnly = true)
    public List<ApiDtos.ProviderView> searchProviders(@RequestParam(required = false) String categorySlug,
                                                       @RequestParam(required = false) String area,
                                                       @RequestParam(required = false, defaultValue = "false") boolean verified,
                                                       @RequestParam(required = false, defaultValue = "false") boolean emergency,
                                                       @RequestParam(required = false, defaultValue = "0") double minRating) {
        return providers.findAllByActiveTrueOrderByTrustScoreDesc().stream()
                .filter(p -> categorySlug == null || categorySlug.isBlank() || (p.getPrimaryCategory() != null && categorySlug.equals(p.getPrimaryCategory().getSlug())))
                .filter(p -> area == null || area.isBlank() || p.getPrimaryArea().equalsIgnoreCase(area))
                .filter(p -> !verified || "VERIFIED".equals(p.getVerificationStatus()))
                .filter(p -> !emergency || (p.isEmergencyAvailable() && p.isAvailableToday()))
                .filter(p -> p.getRatingAverage() >= minRating)
                .map(ApiDtos.ProviderView::from).toList();
    }

    @GetMapping("/api/providers/{id}")
    @Transactional(readOnly = true)
    public ApiDtos.ProviderView provider(@PathVariable String id) { return ApiDtos.ProviderView.from(findProvider(id)); }

    @GetMapping("/api/providers/{id}/skill-passport")
    @Transactional(readOnly = true)
    public ApiDtos.ProviderView skillPassport(@PathVariable String id) { return ApiDtos.ProviderView.from(findProvider(id)); }

    @GetMapping("/api/providers/{id}/availability")
    @Transactional(readOnly = true)
    public AvailabilityView availability(@PathVariable String id) {
        ProviderProfile p = findProvider(id);
        return new AvailabilityView(p.isAvailableToday(), p.isEmergencyAvailable(), p.getPrimaryArea());
    }

    @PutMapping("/api/providers/{id}/availability")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    @Transactional
    public AvailabilityView updateAvailability(@PathVariable String id, @Valid @RequestBody ApiDtos.AvailabilityRequest req,
                                               @AuthenticationPrincipal UserEntity actor) {
        ProviderProfile p = findProvider(id);
        if (actor.getRole().name().equals("PROVIDER") && !actor.getId().equals(p.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only change your own provider availability.");
        p.setAvailableToday(req.availableToday()); p.setEmergencyAvailable(req.emergencyAvailable());
        providers.save(p);
        return new AvailabilityView(p.isAvailableToday(), p.isEmergencyAvailable(), p.getPrimaryArea());
    }

    private ProviderProfile findProvider(String id) {
        return providers.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider profile was not found."));
    }
    public record AvailabilityView(boolean availableToday, boolean emergencyAvailable, String serviceArea) {}
}
