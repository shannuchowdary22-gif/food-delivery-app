package com.zomiggy.repository;

import com.zomiggy.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(String orderId);
    Optional<Delivery> findByPartnerUserMobileAndOrderId(String mobile, String orderId);
    @EntityGraph(attributePaths = {"order", "partner", "partner.user"})
    List<Delivery> findByPartnerIsNotNullAndStatusNot(String status);
}
