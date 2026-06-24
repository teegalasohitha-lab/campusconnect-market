package com.campusconnect.market.service;

import com.campusconnect.market.dto.request.ReviewRequest;
import com.campusconnect.market.model.*;
import com.campusconnect.market.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Product reviews and ratings business logic.
 */
@Service
@Transactional
public class ReviewService {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ProductRepository productRepository;

    public Review addReview(User user, ReviewRequest request) {
        if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), user.getId())) {
            throw new RuntimeException("You have already reviewed this product");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        return reviewRepository.getAverageRatingByProductId(productId);
    }

    @Transactional(readOnly = true)
    public long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    @Transactional(readOnly = true)
    public boolean hasUserReviewed(Long productId, Long userId) {
        return reviewRepository.existsByProductIdAndUserId(productId, userId);
    }
}
