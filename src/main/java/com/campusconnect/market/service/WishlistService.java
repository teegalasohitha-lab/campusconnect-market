package com.campusconnect.market.service;

import com.campusconnect.market.model.*;
import com.campusconnect.market.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Wishlist business logic.
 */
@Service
@Transactional
public class WishlistService {

    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Wishlist> getWishlist(User user) {
        return wishlistRepository.findByUser(user);
    }

    /** Toggle: add to wishlist if absent, remove if present */
    public String toggleWishlist(User user, Long productId) {
        if (wishlistRepository.existsByUserAndProductId(user, productId)) {
            wishlistRepository.deleteByUserAndProductId(user, productId);
            return "removed";
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            wishlistRepository.save(
                    Wishlist.builder().user(user).product(product).build());
            return "added";
        }
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(User user, Long productId) {
        return wishlistRepository.existsByUserAndProductId(user, productId);
    }

    @Transactional(readOnly = true)
    public long getWishlistCount(User user) {
        return wishlistRepository.findByUser(user).size();
    }
}
