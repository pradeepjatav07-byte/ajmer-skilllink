package com.nexoraa.skilllink.api;

import com.nexoraa.skilllink.service.FairQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FairQuoteController {
    private final FairQuoteService fairQuoteService;
    @GetMapping("/api/fair-quotes")
    public ApiDtos.FairQuoteResponse quote(@RequestParam String categorySlug,
                                           @RequestParam(required = false) String area,
                                           @RequestParam(defaultValue = "false") boolean emergency) {
        return fairQuoteService.quote(categorySlug, area, emergency);
    }
}
