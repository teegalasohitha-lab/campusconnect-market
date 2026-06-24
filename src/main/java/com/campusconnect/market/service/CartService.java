package com.campusconnect.market.service;

import com.campusconnect.market.model.*;
import com.campusconnect.market.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Shopping cart business logic.
 */
@Service
@Transactional
public class CartService {

    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }

    /** Add product to cart or increment quantity if already present */
    public CartItem addToCart(User user, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        return cartItemRepository.findByUserAndProductId(user, productId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> cartItemRepository.save(
                        CartItem.builder().user(user).product(product).quantity(quantity).build()));
    }

    /** Update quantity of a cart item */
    public CartItem updateQuantity(User user, Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeFromCart(User user, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized");
        }
        cartItemRepository.delete(item);
    }

    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    @Transactional(readOnly = true)
    public BigDecimal getCartTotal(User user) {
        return cartItemRepository.findByUser(user).stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public long getCartCount(User user) {
        return cartItemRepository.countByUser(user);
    }
}
