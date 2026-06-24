package com.campusconnect.market.controller;

import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.model.*;
import com.campusconnect.market.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin-only management endpoints.
 * Base path: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    /** GET /api/admin/stats */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", (long) userService.findAll().size());
        stats.put("totalProducts", productService.countAll());
        stats.put("totalOrders", orderService.countAll());
        stats.put("totalRevenue", orderService.getTotalRevenue());
        stats.put("recentUsers",
                userService.findAll().stream().limit(10).collect(java.util.stream.Collectors.toList()));
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /** GET /api/admin/users */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.findAll()));
    }

    /** GET /api/admin/users/sellers */
    @GetMapping("/users/sellers")
    public ResponseEntity<ApiResponse<List<User>>> getSellers() {
        return ResponseEntity.ok(ApiResponse.success(userService.findByRole(Role.SELLER)));
    }

    /** GET /api/admin/users/customers */
    @GetMapping("/users/customers")
    public ResponseEntity<ApiResponse<List<User>>> getCustomers() {
        return ResponseEntity.ok(ApiResponse.success(userService.findByRole(Role.CUSTOMER)));
    }

    /** POST /api/admin/users/{id}/toggle — Enable/disable user */
    @PostMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<String>> toggleUser(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok(ApiResponse.success("OK", "User status toggled"));
    }

    /** GET /api/admin/products */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(productService.getActiveProducts(page, size, "createdAt")));
    }

    /** GET /api/admin/orders */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders()));
    }

    /** PATCH /api/admin/orders/{id}/status */
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            Order order = orderService.updateStatus(id, body.get("status"));
            return ResponseEntity.ok(ApiResponse.success(order, "Order status updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
