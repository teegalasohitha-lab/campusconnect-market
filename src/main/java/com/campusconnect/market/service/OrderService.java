package com.campusconnect.market.service;

import com.campusconnect.market.dto.request.OrderRequest;
import com.campusconnect.market.model.*;
import com.campusconnect.market.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Order placement and management business logic.
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    /** Place an order from the customer's current cart */
    public Order placeOrder(User buyer, OrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUser(buyer);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate stock and compute total
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getTitle());
            }

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItems.add(orderItem);

            total = total.add(product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            // Deduct stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(buyer)
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .deliveryAddress(request.getDeliveryAddress())
                .paymentMethod(request.getPaymentMethod())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Link order items to order
        orderItems.forEach(item -> item.setOrder(savedOrder));
        savedOrder.setItems(orderItems);

        // Clear cart after placing order
        cartItemRepository.deleteByUser(buyer);

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByBuyer(User buyer) {
        return orderRepository.findByUserOrderByCreatedAtDesc(buyer);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersForSeller(User seller) {
        return orderRepository.findOrdersForSeller(seller);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll(
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }

    /** Update order status (seller or admin) */
    public Order updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    public long countAll() {
        return orderRepository.count();
    }

    public long countPending() {
        return orderRepository.countByStatus(OrderStatus.PENDING);
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "CCM-" + timestamp;
    }
}
