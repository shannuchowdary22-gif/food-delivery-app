package com.zomiggy.dto;

import com.zomiggy.dto.ApiDtos.Address;
import java.time.Instant;

public final class DeliveryDtos {
    private DeliveryDtos() {}
    public record PartnerResponse(Long id, String name, String phone, String vehicleNumber, String vehicleType) {}
    public record DeliveryResponse(Long id, String orderId, String status, PartnerResponse deliveryPartner, Address partnerLocation, Instant updatedAt) {}
    public record TrackingResponse(String orderId, String status, String deliveryStatus, boolean trackingActive, int etaMinutes, double distanceKm, double speedKph, PartnerResponse deliveryPartner, Address partnerLocation, Address origin, Address destination, Instant updatedAt) {}
    public record LocationRequest(Double latitude, Double longitude, Double speedKph) {}
    public record TrackingUpdate(String orderId, String status, Double latitude, Double longitude, Double speedKph, Double heading, Double distanceRemainingKm, Integer etaMinutes) {}
}
