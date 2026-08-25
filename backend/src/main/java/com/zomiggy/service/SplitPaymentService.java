package com.zomiggy.service;

import com.zomiggy.dto.ApiDtos.OrderItemRequest;
import com.zomiggy.dto.SplitPaymentDtos.*;
import com.zomiggy.entity.SplitPayment;
import com.zomiggy.entity.SplitPaymentParticipant;
import com.zomiggy.repository.CouponRepository;
import com.zomiggy.repository.DishRepository;
import com.zomiggy.repository.SplitPaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SplitPaymentService {
    private final SplitPaymentRepository payments;
    private final DishRepository dishes;
    private final CouponRepository coupons;
    private final AuthService auth;
    private final PasswordEncoder passwordEncoder;
    public SplitPaymentService(SplitPaymentRepository payments, DishRepository dishes, CouponRepository coupons, AuthService auth, PasswordEncoder passwordEncoder) { this.payments = payments; this.dishes = dishes; this.coupons = coupons; this.auth = auth; this.passwordEncoder = passwordEncoder; }

    @Transactional
    public Response create(String mobile, CreateRequest request) {
        long totalPaise = calculateTotal(request.items(), request.couponCode()) * 100L;
        SplitPayment payment = new SplitPayment(UUID.randomUUID().toString(), auth.current(mobile), totalPaise, request.numberOfParticipants());
        long base = totalPaise / request.numberOfParticipants();
        long remainder = totalPaise % request.numberOfParticipants();
        for (int index = 0; index < request.numberOfParticipants(); index++) payment.addParticipant(new SplitPaymentParticipant("", base + (index == request.numberOfParticipants() - 1 ? remainder : 0)));
        return view(payments.save(payment));
    }

    @Transactional
    public Response updateParticipant(String mobile, String id, Long participantId, ParticipantRequest request) {
        SplitPayment payment = owned(mobile, id);
        SplitPaymentParticipant participant = payment.getParticipants().stream().filter(item -> Objects.equals(item.getId(), participantId)).findFirst().orElseThrow(() -> new NoSuchElementException("Participant not found"));
        if (participant.getStatus().equals("PAID")) throw new IllegalArgumentException("Participant is already paid");
        if (!passwordEncoder.matches(request.pin(), payment.getUser().getOrderPinHash())) throw new IllegalArgumentException("Invalid UPI PIN");
        participant.setUpiId(request.upiId());
        participant.markPaid();
        long paid = payment.getParticipants().stream().filter(item -> item.getStatus().equals("PAID")).mapToLong(item -> item.getAmountPaise()).sum();
        payment.setAmountPaidPaise(paid);
        payment.setStatus(paid == payment.getTotalPaise() ? "PAID" : "PENDING");
        return view(payments.save(payment));
    }

    @Transactional(readOnly = true)
    public Response get(String mobile, String id) { return view(owned(mobile, id)); }
    @Transactional
    public Response complete(String mobile, String id) { SplitPayment payment = owned(mobile, id); if (!payment.getStatus().equals("PAID")) throw new IllegalArgumentException("All participants must pay before completion"); return view(payment); }
    @Transactional(readOnly = true)
    public boolean isPaid(String mobile, String id, long expectedPaise) { SplitPayment payment = owned(mobile, id); return payment.getStatus().equals("PAID") && payment.getTotalPaise() == expectedPaise; }
    private SplitPayment owned(String mobile, String id) { return payments.findByIdAndUserId(id, auth.current(mobile).getId()).orElseThrow(() -> new NoSuchElementException("Split payment not found")); }
    private int calculateTotal(List<OrderItemRequest> items, String couponCode) {
        int subtotal = 0; for (OrderItemRequest item : items) { Long dishId = Objects.requireNonNull(item.dishId()); subtotal += dishes.findById(dishId).orElseThrow(() -> new IllegalArgumentException("Dish not found: " + dishId)).getPrice() * item.quantity(); }
        if (couponCode == null || couponCode.isBlank()) return subtotal;
        String normalizedCode = Objects.requireNonNull(couponCode).toUpperCase(Locale.ROOT);
        int discount = normalizedCode.equals("SAVE100") ? Math.min(subtotal, 100) : coupons.findById(Objects.requireNonNull(normalizedCode)).orElseThrow(() -> new IllegalArgumentException("Invalid coupon")).discount(subtotal);
        if (discount == 0) throw new IllegalArgumentException("Coupon minimum order not met");
        return Math.max(0, subtotal - discount);
    }
    private Response view(SplitPayment payment) {
        BigDecimal total = money(payment.getTotalPaise()), paid = money(payment.getAmountPaidPaise());
        return new Response(payment.getId(), total, payment.getNumberOfParticipants(), paid, total.subtract(paid), payment.getStatus(), payment.getParticipants().stream().map(item -> new ParticipantResponse(item.getId(), "zomiggy://split/" + payment.getId() + "/participant/" + item.getId(), item.getUpiId(), money(item.getAmountPaise()), item.getStatus(), item.getPaidAt())).toList());
    }
    private BigDecimal money(long paise) { return BigDecimal.valueOf(paise, 2).setScale(2, RoundingMode.UNNECESSARY); }
}
