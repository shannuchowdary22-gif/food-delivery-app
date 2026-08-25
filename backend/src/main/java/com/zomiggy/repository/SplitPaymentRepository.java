package com.zomiggy.repository;

import com.zomiggy.entity.SplitPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SplitPaymentRepository extends JpaRepository<SplitPayment, String> {
    Optional<SplitPayment> findByIdAndUserId(String id, Long userId);
}
