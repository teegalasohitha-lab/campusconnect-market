package com.campusconnect.market.repository;

import com.campusconnect.market.model.PreOrder;
import com.campusconnect.market.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreOrderRepository extends JpaRepository<PreOrder, Long> {

    List<PreOrder> findByBuyerOrderByCreatedAtDesc(User buyer);

    List<PreOrder> findByProductSellerOrderByCreatedAtDesc(User seller);

    boolean existsByProductIdAndBuyerId(Long productId, Long buyerId);
}
