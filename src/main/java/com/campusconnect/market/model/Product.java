package com.campusconnect.market.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A product listed on CampusConnect Market.
 * Supports normal listings, Student Exchange items, and Pre-Order products.
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    /** Seller who listed this product */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    /** Category this product belongs to */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    /** Campus or locality for nearby discovery */
    @Column(name = "location", length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", length = 20, nullable = false)
    @Builder.Default
    private ProductType type = ProductType.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_condition", length = 20)
    @Builder.Default
    private ProductCondition condition = ProductCondition.NEW;

    /** Main product image URL */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    // ── Pre-Order Feature Details ───────────────────────────────
    @Column(name = "pre_order_deadline")
    private LocalDate preOrderDeadline;

    @Column(name = "min_pre_order_qty")
    @Builder.Default
    private Integer minPreOrderQty = 1;

    // ── Status ──────────────────────────────────────────────────
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
