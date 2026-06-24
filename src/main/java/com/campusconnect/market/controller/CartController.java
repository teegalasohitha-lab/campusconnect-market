package com.campusconnect.market.controller;

import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.model.*;
import com.campusconnect.market.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired private CartService cartService;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItem>>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartItems(user)));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartItem>> addToCart(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Long productId = Long.valueOf(body.get("productId").toString());
            int quantity = body.containsKey("quantity") ?
                    Integer.parseInt(body.get("quantity").toString()) : 1;
            CartItem item = cartService.addToCart(user, productId, quantity);
            return ResponseEntity.ok(ApiResponse.success(item, "Added to cart"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItem>> updateQuantity(
            @PathVariable Long cartItemId,
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        CartItem item = cartService.updateQuantity(user, cartItemId, body.get("quantity"));
        return ResponseEntity.ok(ApiResponse.success(item, "Cart updated"));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        cartService.removeFromCart(user, cartItemId);
        return ResponseEntity.ok(ApiResponse.success("OK", "Item removed from cart"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        cartService.clearCart(user);
        return ResponseEntity.ok(ApiResponse.success("OK", "Cart cleared"));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotal(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartTotal(user)));
    }
}
