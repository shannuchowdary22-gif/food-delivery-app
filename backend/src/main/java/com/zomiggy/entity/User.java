package com.zomiggy.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.Instant;

@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(name="uk_user_mobile", columnNames="mobile"))
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=10) private String mobile;
    @Column(nullable=false) private String name = "Foodie User";
    private String address;
    @Column(nullable=false) private int coins;
    @Column(nullable=false) private String orderPinHash = new BCryptPasswordEncoder().encode("1234");
    @Enumerated(EnumType.STRING) @Column(nullable=false, columnDefinition="varchar(32)") private Role role = Role.USER;
    @Column(nullable=false, updatable=false) private Instant createdAt = Instant.now();
    public enum Role { USER, ADMIN, DELIVERY_PARTNER }
    protected User() {}
    public User(String mobile) { this.mobile=mobile; }
    @PostLoad private void ensureOrderPinHash() { if (orderPinHash == null) orderPinHash = new BCryptPasswordEncoder().encode("1234"); }
    public Long getId(){return id;} public String getMobile(){return mobile;} public String getName(){return name;} public void setName(String v){name=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;} public int getCoins(){return coins;} public void addCoins(int v){coins+=v;} public Role getRole(){return role;} public void setRole(Role value){role=value;} public String getOrderPinHash(){return orderPinHash;}
}