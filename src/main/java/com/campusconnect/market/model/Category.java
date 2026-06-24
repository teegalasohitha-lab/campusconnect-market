package com.campusconnect.market.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Product category (supports parent-child hierarchy for subcategories).
 */
@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** FontAwesome icon class, e.g. "fa-book" */
    @Column(length = 50)
    private String icon;

    /** Hex color for category card UI */
    @Column(length = 10)
    private String color;

    /** Self-referencing for subcategories (e.g. Student Exchange Hub) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "subcategories" })
    private Category parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Category> subcategories;
}
