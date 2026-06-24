package com.campusconnect.market.service;

import com.campusconnect.market.dto.request.ProductRequest;
import com.campusconnect.market.model.*;
import com.campusconnect.market.repository.CategoryRepository;
import com.campusconnect.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for product listings, search, and management.
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    /** Create a new product listing */
    public Product createProduct(ProductRequest request, User seller) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElse(null);
        }

        ProductCondition condition = ProductCondition.NEW;
        if (request.getCondition() != null) {
            try {
                condition = ProductCondition.valueOf(request.getCondition().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        ProductType type = ProductType.NORMAL;
        if (request.getType() != null) {
            try {
                type = ProductType.valueOf(request.getType().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .seller(seller)
                .category(category)
                .location(request.getLocation())
                .type(type)
                .condition(condition)
                .imageUrl(request.getImageUrl())
                .preOrderDeadline(request.getPreOrderDeadline())
                .minPreOrderQty(request.getMinPreOrderQty() != null ? request.getMinPreOrderQty() : 1)
                .isActive(true)
                .build();

        return productRepository.save(product);
    }

    /** Update an existing product */
    public Product updateProduct(Long productId, ProductRequest request, User seller) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("Not authorized to update this product");
        }

        if (request.getTitle() != null)
            product.setTitle(request.getTitle());
        if (request.getDescription() != null)
            product.setDescription(request.getDescription());
        if (request.getPrice() != null)
            product.setPrice(request.getPrice());
        if (request.getStock() != null)
            product.setStock(request.getStock());
        if (request.getImageUrl() != null)
            product.setImageUrl(request.getImageUrl());
        if (request.getLocation() != null)
            product.setLocation(request.getLocation());
        if (request.getType() != null) {
            try {
                product.setType(ProductType.valueOf(request.getType().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .ifPresent(product::setCategory);
        }

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> getActiveProducts(int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy != null ? sortBy : "createdAt");
        return productRepository.findByIsActive(true, PageRequest.of(page, size, sort));
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Long categoryId, String location,
            ProductType type, java.util.Collection<ProductCondition> conditions, int page, int size) {
        return productRepository.findWithFilters(keyword, categoryId, location, type, conditions,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Product> findBySeller(User seller) {
        return productRepository.findBySeller(seller);
    }

    @Transactional(readOnly = true)
    public Page<Product> getStudentExchangeItems(int page, int size) {
        return productRepository.findByTypeAndIsActive(ProductType.EXCHANGE, true,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional(readOnly = true)
    public Page<Product> getPreOrderProducts(int page, int size) {
        return productRepository.findByTypeAndIsActive(ProductType.PREORDER, true,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public void deactivateProduct(Long productId, User seller) {
        Product product = findById(productId);
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("Not authorized");
        }
        product.setIsActive(false);
        productRepository.save(product);
    }

    public long countAll() {
        return productRepository.count();
    }

    public long countActive() {
        return productRepository.countByIsActive(true);
    }
}
