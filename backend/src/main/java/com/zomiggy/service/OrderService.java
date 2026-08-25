package com.zomiggy.service;

import com.zomiggy.dto.ApiDtos.OrderItemRequest;
import com.zomiggy.dto.ApiDtos.OrderRequest;
import com.zomiggy.dto.ApiDtos.OrderResponse;
import com.zomiggy.entity.Order;
import com.zomiggy.entity.OrderItem;
import com.zomiggy.repository.CouponRepository;
import com.zomiggy.repository.DishRepository;
import com.zomiggy.repository.OrderRepository;
import com.zomiggy.repository.SplitPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final DishRepository dishes;
    private final CouponRepository coupons;
    private final AuthService auth;
    private final PasswordEncoder passwordEncoder;
    private final SplitPaymentRepository splitPayments;
    private final DeliveryService deliveryService;

    public OrderService(OrderRepository orders, DishRepository dishes, CouponRepository coupons, AuthService auth, PasswordEncoder passwordEncoder, SplitPaymentRepository splitPayments, DeliveryService delivery) {
        this.orders = orders;
        this.dishes = dishes;
        this.coupons = coupons;
        this.auth = auth;
        this.passwordEncoder = passwordEncoder;
        this.splitPayments = splitPayments;
        this.deliveryService = delivery;
    }

    @Transactional
    public OrderResponse create(String mobile, OrderRequest request) {
        String paymentMethod = Objects.requireNonNull(request.paymentMethod()).toUpperCase();
        if (!Set.of("UPI", "CREDIT", "DEBIT", "COD", "SPLIT_PAYMENT").contains(paymentMethod)) throw new IllegalArgumentException("Unsupported payment method");

        int subtotal = 0;
        List<OrderItem> loaded = new ArrayList<>();
        for (OrderItemRequest itemRequest : Objects.requireNonNull(request.items())) {
            Long dishId = Objects.requireNonNull(itemRequest.dishId());
            var dish = dishes.findById(dishId).orElseThrow(() -> new IllegalArgumentException("Dish not found: " + dishId));
            subtotal += dish.getPrice() * itemRequest.quantity();
            loaded.add(new OrderItem(dish, itemRequest.quantity()));
        }

        int discount = 0;
        String couponCode = request.couponCode();
        if (couponCode != null && !couponCode.isBlank()) {
                        String normalizedCouponCode = couponCode.toUpperCase(Locale.ROOT);
                        if (normalizedCouponCode == null) {
                            throw new IllegalArgumentException("Coupon code cannot be blank");
                        }
                        discount = normalizedCouponCode.equals("SAVE100")
                                ? Math.min(subtotal, 100)
                                : coupons.findById(normalizedCouponCode)
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid coupon"))
                                    .discount(subtotal);
            if (discount == 0) throw new IllegalArgumentException("Coupon minimum order not met");
        }

        var user = auth.current(Objects.requireNonNull(mobile));
        int total = Math.max(0, subtotal - discount);
        if (paymentMethod.equals("SPLIT_PAYMENT")) {
            if (request.splitPaymentId() == null || request.splitCount() < 1) throw new IllegalArgumentException("A completed split payment is required");
            var split = splitPayments.findByIdAndUserId(request.splitPaymentId(), user.getId()).orElseThrow(() -> new IllegalArgumentException("Split payment not found"));
            if (!split.getStatus().equals("PAID") || split.getTotalPaise() != total * 100L) throw new IllegalArgumentException("Split payment is incomplete or amount does not match");
        }
        if (!passwordEncoder.matches(request.pin(), user.getOrderPinHash())) throw new IllegalArgumentException("Invalid order PIN");
        var delivery = Objects.requireNonNull(request.deliveryAddress());
        String deliveryAddress = delivery.label();
        var order = new Order("ODR" + (100000 + new Random().nextInt(900000)), user, paymentMethod, total, deliveryAddress, delivery.latitude(), delivery.longitude());
        order.setSplitPaymentId(request.splitPaymentId());
        loaded.forEach(order::addItem);
        user.addCoins(50);
        orders.save(order);
        deliveryService.createForOrder(order);
        return response(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> history(String mobile) {
        var user = auth.current(Objects.requireNonNull(mobile));
        Long userId = Objects.requireNonNull(user.getId());
        return orders.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::response).toList();
    }

    public OrderResponse response(Order order) {
        String items = order.getItems().stream().map(item -> item.getDish().getName() + " x" + item.getQuantity()).reduce((a, b) -> a + ", " + b).orElse("");
        return new OrderResponse(order.getId(), order.getStatus(), items, order.getPaymentMethod(), order.getTotal(), order.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public Order owned(String mobile, String id) {
        var user = auth.current(Objects.requireNonNull(mobile));
        return orders.findByIdAndUserId(Objects.requireNonNull(id), Objects.requireNonNull(user.getId())).orElseThrow(() -> new NoSuchElementException("Order not found"));
    }

}