package com.zomiggy.service;

import com.zomiggy.dto.ApiDtos.Address;
import com.zomiggy.dto.DeliveryDtos.*;
import com.zomiggy.entity.*;
import com.zomiggy.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Comparator;
import java.util.NoSuchElementException;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveries;
    private final DeliveryPartnerRepository partners;
    private final SimpMessagingTemplate messaging;
    public DeliveryService(DeliveryRepository deliveries, DeliveryPartnerRepository partners, SimpMessagingTemplate messaging) { this.deliveries = deliveries; this.partners = partners; this.messaging = messaging; }

    @Transactional
    public Delivery createForOrder(Order order) {
        Delivery delivery = new Delivery(order);
        assignNext(delivery);
        return deliveries.save(delivery);
    }

    @Transactional
    public DeliveryResponse reject(String mobile, String orderId) { Delivery delivery = assigned(mobile, orderId); delivery.reject(); assignNext(delivery); return response(deliveries.save(delivery)); }
    @Transactional
    public DeliveryResponse accept(String mobile, String orderId) { Delivery delivery = assigned(mobile, orderId); require(delivery.getStatus(), "OFFERED"); delivery.setStatus("ACCEPTED"); return response(deliveries.save(delivery)); }
    @Transactional
    public DeliveryResponse updateStatus(String mobile, String orderId, String next) { Delivery delivery = assigned(mobile, orderId); return response(changeStatus(delivery, next)); }
    @Transactional
    public DeliveryResponse updateLocation(String mobile, String orderId, LocationRequest request) { Delivery delivery = assigned(mobile, orderId); if (!delivery.getStatus().equals("PICKED_UP") && !delivery.getStatus().equals("OUT_FOR_DELIVERY") && !delivery.getStatus().equals("ARRIVING")) throw new IllegalArgumentException("Tracking is not active until pickup"); return response(updateLocation(delivery, request.latitude(), request.longitude(), request.speedKph())); }

    @Transactional
    public void simulateStatus(String orderId, String next) { deliveries.findByOrderId(orderId).ifPresent(delivery -> changeStatus(delivery, next)); }

    @Transactional
    public void simulateLocation(String orderId, Double latitude, Double longitude) { deliveries.findByOrderId(orderId).ifPresent(delivery -> updateLocation(delivery, latitude, longitude, 25d)); }
    @Transactional(readOnly = true)
    public TrackingResponse tracking(Order order) { Delivery delivery = deliveries.findByOrderId(order.getId()).orElseThrow(() -> new NoSuchElementException("Delivery is not assigned yet")); DeliveryPartner partner = delivery.getPartner(); Address destination = new Address(order.getDeliveryAddress(), order.getDeliveryLatitude(), order.getDeliveryLongitude()); Address origin = new Address("Restaurant", 17.44, 78.48); boolean active = delivery.getStatus().equals("PICKED_UP") || delivery.getStatus().equals("OUT_FOR_DELIVERY") || delivery.getStatus().equals("ARRIVING"); double distance = delivery.getDistanceRemainingKm() == null ? 0 : delivery.getDistanceRemainingKm(); int eta = delivery.getEtaMinutes() == null ? 0 : delivery.getEtaMinutes(); PartnerResponse view = partner == null ? null : new PartnerResponse(partner.getId(), partner.getUser().getName(), partner.getUser().getMobile(), partner.getVehicleNumber(), partner.getVehicleType()); Address location = delivery.getLatitude() == null ? null : new Address("Partner", delivery.getLatitude(), delivery.getLongitude()); return new TrackingResponse(order.getId(), order.getStatus(), delivery.getStatus(), active, eta, distance, delivery.getSpeedKph() == null ? 0 : delivery.getSpeedKph(), view, location, origin, destination, delivery.getUpdatedAt()); }
    private DeliveryResponse response(Delivery delivery) { DeliveryPartner partner = delivery.getPartner(); PartnerResponse view = partner == null ? null : new PartnerResponse(partner.getId(), partner.getUser().getName(), partner.getUser().getMobile(), partner.getVehicleNumber(), partner.getVehicleType()); Address location = delivery.getLatitude() == null ? null : new Address("Partner", delivery.getLatitude(), delivery.getLongitude()); return new DeliveryResponse(delivery.getId(), delivery.getOrder().getId(), delivery.getStatus(), view, location, delivery.getUpdatedAt()); }
    private Delivery changeStatus(Delivery delivery, String next) { String current = delivery.getStatus(); if (next.equals("PICKED_UP") && !current.equals("ACCEPTED") || next.equals("OUT_FOR_DELIVERY") && !current.equals("PICKED_UP") || next.equals("ARRIVING") && !current.equals("OUT_FOR_DELIVERY") || next.equals("DELIVERED") && !current.equals("ARRIVING")) throw new IllegalArgumentException("Invalid delivery status transition"); delivery.setStatus(next); Delivery saved = deliveries.save(delivery); publish(saved); return saved; }
    private Delivery updateLocation(Delivery delivery, Double latitude, Double longitude, Double speedKph) { Double previousLatitude = delivery.getLatitude(); Double previousLongitude = delivery.getLongitude(); double distance = distanceKm(latitude, longitude, delivery.getOrder().getDeliveryLatitude(), delivery.getOrder().getDeliveryLongitude()); double speed = speedKph == null ? 0 : Math.max(0, speedKph); int eta = speed > 0 ? (int)Math.ceil(distance / speed * 60) : 0; double heading = previousLatitude == null ? 0 : Math.toDegrees(Math.atan2(longitude - previousLongitude, latitude - previousLatitude)); delivery.setLocation(latitude, longitude, speed, heading, distance, eta); Delivery saved = deliveries.save(delivery); publish(saved); return saved; }
    private void publish(Delivery delivery) { messaging.convertAndSend("/topic/orders/" + delivery.getOrder().getId() + "/tracking", new TrackingUpdate(delivery.getOrder().getId(), delivery.getStatus(), delivery.getLatitude(), delivery.getLongitude(), delivery.getSpeedKph(), delivery.getHeading(), delivery.getDistanceRemainingKm(), delivery.getEtaMinutes())); }
    private Delivery assigned(String mobile, String orderId) { Delivery delivery = deliveries.findByPartnerUserMobileAndOrderId(mobile, orderId).orElseThrow(() -> new NoSuchElementException("Delivery is not assigned to this partner")); return delivery; }
    private void assignNext(Delivery delivery) { partners.findByAvailableTrue().stream().filter(partner -> !delivery.rejected(partner.getId())).min(Comparator.comparingLong(partner -> partner.getId() == null ? Long.MAX_VALUE : partner.getId())).ifPresent(delivery::assign); }
    private void require(String actual, String expected) { if (!actual.equals(expected)) throw new IllegalArgumentException("Delivery must be " + expected); }
    private double distanceKm(double lat1, double lon1, Double lat2, Double lon2) { if (lat2 == null || lon2 == null) return 0; double r=6371, dLat=Math.toRadians(lat2-lat1), dLon=Math.toRadians(lon2-lon1); double a=Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.sin(dLon/2)*Math.sin(dLon/2); return r*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a)); }
}
