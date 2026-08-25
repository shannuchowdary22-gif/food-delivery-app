package com.zomiggy.controller;

import com.zomiggy.dto.SplitPaymentDtos.*;
import com.zomiggy.service.SplitPaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/split")
public class SplitPaymentController {
    private final SplitPaymentService service;
    public SplitPaymentController(SplitPaymentService service) { this.service = service; }
    @PostMapping public Map<String, Object> create(Authentication authentication, @Valid @RequestBody CreateRequest request) { return Map.of("splitPayment", service.create(authentication.getName(), request)); }
    @GetMapping("/{id}") public Map<String, Object> get(Authentication authentication, @PathVariable String id) { return Map.of("splitPayment", service.get(authentication.getName(), id)); }
    @PostMapping("/{id}/participants/{participantId}/pay") public Map<String, Object> pay(Authentication authentication, @PathVariable String id, @PathVariable Long participantId, @Valid @RequestBody ParticipantRequest request) { return Map.of("splitPayment", service.updateParticipant(authentication.getName(), id, participantId, request)); }
    @PostMapping("/{id}/complete") public Map<String, Object> complete(Authentication authentication, @PathVariable String id) { return Map.of("splitPayment", service.complete(authentication.getName(), id)); }
}
