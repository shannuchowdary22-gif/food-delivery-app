package com.zomiggy.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "split_payments")
public class SplitPayment {
    @Id
    @Column(nullable = false, length = 36)
    private String id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;
    @Column(nullable = false)
    private long totalPaise;
    @Column(nullable = false)
    private long amountPaidPaise;
    @Column(nullable = false)
    private int numberOfParticipants;
    @Column(nullable = false)
    private String status = "PENDING";
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();
    @OneToMany(mappedBy = "splitPayment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SplitPaymentParticipant> participants = new ArrayList<>();

    protected SplitPayment() {}
    public SplitPayment(String id, User user, long totalPaise, int numberOfParticipants) {
        this.id = id; this.user = user; this.totalPaise = totalPaise; this.numberOfParticipants = numberOfParticipants;
    }
    public void addParticipant(SplitPaymentParticipant participant) { participants.add(participant); participant.setSplitPayment(this); }
    public String getId() { return id; }
    public User getUser() { return user; }
    public long getTotalPaise() { return totalPaise; }
    public long getAmountPaidPaise() { return amountPaidPaise; }
    public int getNumberOfParticipants() { return numberOfParticipants; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; this.updatedAt = Instant.now(); }
    public void setAmountPaidPaise(long amountPaidPaise) { this.amountPaidPaise = amountPaidPaise; this.updatedAt = Instant.now(); }
    public List<SplitPaymentParticipant> getParticipants() { return participants; }
}
