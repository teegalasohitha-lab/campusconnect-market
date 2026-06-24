package com.campusconnect.market.controller;

import com.campusconnect.market.dto.request.ReviewRequest;
import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.model.*;
import com.campusconnect.market.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private ReviewService reviewService;
    @Autowired private UserService userService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<Review>>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewsByProduct(productId)));
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(@PathVariable Long productId) {
        Map<String, Object> stats = Map.of(
                "averageRating", reviewService.getAverageRating(productId),
                "reviewCount", reviewService.getReviewCount(productId)
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> addReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Review review = reviewService.addReview(user, request);
            return ResponseEntity.ok(ApiResponse.success(review, "Review submitted"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
