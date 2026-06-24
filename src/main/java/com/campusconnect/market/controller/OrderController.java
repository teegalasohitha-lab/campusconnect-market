package com.campusconnect.market.controller;

import com.campusconnect.market.dto.request.OrderRequest;
import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.model.*;
import com.campusconnect.market.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;

    /** POST /api/orders — Customer places order from cart */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Order>> placeOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User buyer = userService.findByEmail(userDetails.getUsername());
            Order order = orderService.placeOrder(buyer, request);
            return ResponseEntity.ok(ApiResponse.success(order, "Order placed successfully! Order #" + order.getOrderNumber()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** GET /api/orders/my — Customer's own orders */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Order>>> myOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrdersByBuyer(user)));
    }

    /** GET /api/orders/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(orderService.findById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** PATCH /api/orders/{id}/status — Seller or Admin updates order status */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
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
