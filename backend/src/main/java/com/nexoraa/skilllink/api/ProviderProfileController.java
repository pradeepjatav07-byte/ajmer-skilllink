package com.nexoraa.skilllink.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoraa.skilllink.domain.ProviderProfile;
import com.nexoraa.skilllink.domain.ServiceCategory;
import com.nexoraa.skilllink.domain.UserEntity;
import com.nexoraa.skilllink.repository.ProviderProfileRepository;
import com.nexoraa.skilllink.repository.ServiceCategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/providers/me/profile")
@RequiredArgsConstructor
public class ProviderProfileController {

    private final ProviderProfileRepository providers;
    private final ServiceCategoryRepository categories;
    private final ObjectMapper objectMapper;

    @GetMapping
    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional(readOnly = true)
    public ApiDtos.ProviderView getMyProfile(
            @AuthenticationPrincipal UserEntity actor) {
        return ApiDtos.ProviderView.from(findMyProfile(actor.getId()));
    }

    @PutMapping
    @PreAuthorize("hasRole('PROVIDER')")
    @Transactional
    public ApiDtos.ProviderView updateMyProfile(
            @Valid @RequestBody ApiDtos.ProviderProfileUpdateRequest request,
            @AuthenticationPrincipal UserEntity actor) {
        ProviderProfile profile = findMyProfile(actor.getId());

        ServiceCategory category = categories.findBySlugAndActiveTrue(request.categorySlug().trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Choose an active service category."));

        profile.setPrimaryCategory(category);
        profile.setPrimaryArea(request.area().trim());
        profile.setExperienceYears(request.experienceYears());

        try {
            List<String> skills = request.skills().stream()
                    .map(String::trim)
                    .distinct()
                    .toList();
            profile.setSkillsJson(objectMapper.writeValueAsString(skills));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not save professional skills.", ex);
        }

        String aboutText = request.aboutText();
        profile.setAboutText(aboutText == null || aboutText.isBlank()
                ? null : aboutText.trim());

        profile.setActive(true);
        return ApiDtos.ProviderView.from(profile);
    }

    private ProviderProfile findMyProfile(String userId) {
        return providers.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Provider profile was not found."));
    }
}