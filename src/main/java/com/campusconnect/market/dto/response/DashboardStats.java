package com.campusconnect.market.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Statistics for dashboard widgets.
 */
@Data
@Builder
public class DashboardStats {
    // Admin stats
    private long totalUsers;
    private long totalSellers;
    private long totalCustomers;
    private long totalProducts;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long pendingOrders;

    // Seller stats
    private long myProducts;
    private long myOrders;
    private BigDecimal myRevenue;

    // Customer stats
    private long myOrderCount;
    private long wishlistCount;
    private long cartCount;
}
