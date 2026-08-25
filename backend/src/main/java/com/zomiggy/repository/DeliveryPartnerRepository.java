package com.zomiggy.repository;

import com.zomiggy.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    Optional<DeliveryPartner> findByUserMobile(String mobile);
    List<DeliveryPartner> findByAvailableTrue();
}
