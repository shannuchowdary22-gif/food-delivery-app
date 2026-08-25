package com.zomiggy.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "split_payment_participants")
public class SplitPaymentParticipant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "split_payment_id", nullable = false)
    private SplitPayment splitPayment;
    @Column(nullable = false, length = 120) private String upiId;
    @Column(nullable = false) private long amountPaise;
    @Column(nullable = false) private String status = "PENDING";
    private Instant paidAt;
    protected SplitPaymentParticipant() {}
    public SplitPaymentParticipant(String upiId, long amountPaise) { this.upiId = upiId; this.amountPaise = amountPaise; }
    void setSplitPayment(SplitPayment splitPayment) { this.splitPayment = splitPayment; }
    public Long getId() { return id; }
    public String getUpiId() { return upiId; }
    public void setUpiId(String upiId) { this.upiId = upiId; }
    public long getAmountPaise() { return amountPaise; }
    public String getStatus() { return status; }
    public Instant getPaidAt() { return paidAt; }
    public void markPaid() { status = "PAID"; paidAt = Instant.now(); }
    public void markFailed() { status = "FAILED"; }
}
