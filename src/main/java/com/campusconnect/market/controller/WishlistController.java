package com.campusconnect.market.controller;

import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.model.*;
import com.campusconnect.market.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired private WishlistService wishlistService;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Wishlist>>> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(user)));
    }

    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<String>> toggle(
            @RequestBody Map<String, Long> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            String action = wishlistService.toggleWishlist(user, body.get("productId"));
            return ResponseEntity.ok(ApiResponse.success(action,
                    action.equals("added") ? "Added to wishlist" : "Removed from wishlist"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkWishlist(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(wishlistService.isInWishlist(user, productId)));
    }
}
