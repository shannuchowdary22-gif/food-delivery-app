package com.zomiggy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "delivery_partners")
public class DeliveryPartner {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(optional = false, fetch = FetchType.LAZY) private User user;
    @Column(nullable = false) private String vehicleNumber;
    @Column(nullable = false) private String vehicleType;
    @Column(nullable = false) private boolean available = true;
    private Double latitude;
    private Double longitude;
    protected DeliveryPartner() {}
    public DeliveryPartner(User user, String vehicleNumber, String vehicleType) { this.user = user; this.vehicleNumber = vehicleNumber; this.vehicleType = vehicleType; }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getVehicleType() { return vehicleType; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean value) { available = value; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public void setLocation(Double latitude, Double longitude) { this.latitude = latitude; this.longitude = longitude; }
}
