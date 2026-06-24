package com.campusconnect.market.controller;

import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.model.*;
import com.campusconnect.market.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Seller dashboard endpoints.
 * Base path: /api/seller
 */
@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    /** GET /api/seller/stats — Stats summary */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats(
            @AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByEmail(userDetails.getUsername());
        List<Order> orders = orderService.getOrdersForSeller(seller);

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("activeProducts", (long) productService.findBySeller(seller).size());
        stats.put("totalSales", (long) orders.size());
        stats.put("totalRevenue", orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        stats.put("pendingOrders", orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count());

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /** GET /api/seller/products — List seller's own products */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Product>>> myProducts(
            @AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(productService.findBySeller(seller)));
    }

    /** GET /api/seller/orders — Orders for seller's products */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<Order>>> myOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersForSeller(seller)));
    }

    /** GET /api/seller/profile */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> profile(
            @AuthenticationPrincipal UserDetails userDetails) {
        User seller = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(seller));
    }
}
