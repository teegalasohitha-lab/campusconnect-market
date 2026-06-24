package com.campusconnect.market.repository;

import com.campusconnect.market.model.Order;
import com.campusconnect.market.model.OrderStatus;
import com.campusconnect.market.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user = :seller AND o.status = 'DELIVERED'")
    BigDecimal getRevenueForSeller(User seller);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = :user")
    long countByUser(User user);

    /** Orders that include products from a specific seller */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items oi WHERE oi.product.seller = :seller ORDER BY o.createdAt DESC")
    List<Order> findOrdersForSeller(User seller);
}
