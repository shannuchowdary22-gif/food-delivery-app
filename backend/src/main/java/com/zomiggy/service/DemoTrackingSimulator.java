package com.zomiggy.service;

import com.zomiggy.entity.Delivery;
import com.zomiggy.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DemoTrackingSimulator {
    private final DeliveryRepository deliveries;
    private final DeliveryService tracking;
    private final boolean enabled;
    private final double simulationSpeed;
    private final Map<Long, Long> startedAt = new ConcurrentHashMap<>();

    public DemoTrackingSimulator(DeliveryRepository deliveries, DeliveryService tracking,
                                 @Value("${tracking.demo-mode:true}") boolean enabled,
                                 @Value("${tracking.simulation-speed:1.0}") double simulationSpeed) {
        this.deliveries = deliveries;
        this.tracking = tracking;
        this.enabled = enabled;
        this.simulationSpeed = Math.max(0.1, simulationSpeed);
    }

    @Scheduled(fixedDelayString = "${tracking.update-interval-ms:1000}")
    @Transactional
    public void advance() {
        if (!enabled) return;
        deliveries.findByPartnerIsNotNullAndStatusNot("DELIVERED").forEach(this::advanceDelivery);
    }

    private void advanceDelivery(Delivery delivery) {
        if (delivery.getOrder().getDeliveryLatitude() == null || delivery.getOrder().getDeliveryLongitude() == null) return;
        Long deliveryId = delivery.getId();
        long start = startedAt.computeIfAbsent(deliveryId, ignored -> System.currentTimeMillis());
        double elapsed = (System.currentTimeMillis() - start) / 1000d * simulationSpeed;
        if ("OFFERED".equals(delivery.getStatus()) && elapsed >= 5) {
            tracking.simulateStatus(delivery.getOrder().getId(), "ACCEPTED");
            tracking.simulateStatus(delivery.getOrder().getId(), "PICKED_UP");
            return;
        }
        if (!"PICKED_UP".equals(delivery.getStatus()) && !"OUT_FOR_DELIVERY".equals(delivery.getStatus()) && !"ARRIVING".equals(delivery.getStatus())) return;
        double progress = Math.min(1, Math.max(0, (elapsed - 5) / 30d));
        if (progress >= 1) {
            tracking.simulateLocation(delivery.getOrder().getId(), delivery.getOrder().getDeliveryLatitude(), delivery.getOrder().getDeliveryLongitude());
            if (!"ARRIVING".equals(delivery.getStatus())) tracking.simulateStatus(delivery.getOrder().getId(), "ARRIVING");
            tracking.simulateStatus(delivery.getOrder().getId(), "DELIVERED");
            startedAt.remove(deliveryId);
            return;
        }
        if (progress > 0.75 && "OUT_FOR_DELIVERY".equals(delivery.getStatus())) tracking.simulateStatus(delivery.getOrder().getId(), "ARRIVING");
        else if ("PICKED_UP".equals(delivery.getStatus())) tracking.simulateStatus(delivery.getOrder().getId(), "OUT_FOR_DELIVERY");
        double originLat = 17.44;
        double originLng = 78.48;
        double latitude = originLat + (delivery.getOrder().getDeliveryLatitude() - originLat) * progress;
        double longitude = originLng + (delivery.getOrder().getDeliveryLongitude() - originLng) * progress;
        tracking.simulateLocation(delivery.getOrder().getId(), latitude, longitude);
    }
}
