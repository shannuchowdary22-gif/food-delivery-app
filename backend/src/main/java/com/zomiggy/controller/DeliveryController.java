package com.zomiggy.controller;

import com.zomiggy.dto.DeliveryDtos.*;
import com.zomiggy.service.DeliveryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {
    private final DeliveryService service;
    public DeliveryController(DeliveryService service) { this.service = service; }
    @PostMapping("/orders/{orderId}/accept") public Map<String,Object> accept(Authentication auth, @PathVariable String orderId) { requirePartner(auth); return Map.of("delivery", service.accept(auth.getName(), orderId)); }
    @PostMapping("/orders/{orderId}/reject") public Map<String,Object> reject(Authentication auth, @PathVariable String orderId) { requirePartner(auth); return Map.of("delivery", service.reject(auth.getName(), orderId)); }
    @PostMapping("/orders/{orderId}/status/{status}") public Map<String,Object> status(Authentication auth, @PathVariable String orderId, @PathVariable String status) { requirePartner(auth); return Map.of("delivery", service.updateStatus(auth.getName(), orderId, status)); }
    @PostMapping("/orders/{orderId}/location") public Map<String,Object> location(Authentication auth, @PathVariable String orderId, @RequestBody LocationRequest request) { requirePartner(auth); return Map.of("delivery", service.updateLocation(auth.getName(), orderId, request)); }
    private void requirePartner(Authentication auth) { if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_DELIVERY_PARTNER"))) throw new org.springframework.security.access.AccessDeniedException("Delivery partner role required"); }
}
