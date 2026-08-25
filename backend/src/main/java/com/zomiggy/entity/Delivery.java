package com.zomiggy.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY) private Order order;
    @ManyToOne(fetch = FetchType.LAZY) private DeliveryPartner partner;
    @Column(nullable = false) private String status = "SEARCHING";
    @ElementCollection private Set<Long> rejectedPartnerIds = new HashSet<>();
    private Double latitude;
    private Double longitude;
    private Double speedKph;
    private Double heading;
    private Double distanceRemainingKm;
    private Integer etaMinutes;
    private Instant updatedAt = Instant.now();
    protected Delivery() {}
    public Delivery(Order order) { this.order = order; }
    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public DeliveryPartner getPartner() { return partner; }
    public void assign(DeliveryPartner value) { partner = value; status = "OFFERED"; updatedAt = Instant.now(); }
    public void reject() { if (partner != null) rejectedPartnerIds.add(partner.getId()); partner = null; status = "SEARCHING"; updatedAt = Instant.now(); }
    public boolean rejected(Long id) { return rejectedPartnerIds.contains(id); }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; updatedAt = Instant.now(); }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getSpeedKph() { return speedKph; }
    public Double getHeading() { return heading; }
    public Double getDistanceRemainingKm() { return distanceRemainingKm; }
    public Integer getEtaMinutes() { return etaMinutes; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setLocation(Double latitude, Double longitude, Double speedKph, Double heading, Double distanceRemainingKm, Integer etaMinutes) { this.latitude = latitude; this.longitude = longitude; this.speedKph = speedKph; this.heading = heading; this.distanceRemainingKm = distanceRemainingKm; this.etaMinutes = etaMinutes; updatedAt = Instant.now(); }
}
