package com.campusconnect.market.controller;

import com.campusconnect.market.dto.request.ProductRequest;
import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.model.Product;
import com.campusconnect.market.model.User;
import com.campusconnect.market.service.FileStorageService;
import com.campusconnect.market.service.ProductService;
import com.campusconnect.market.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST endpoints for product browsing, search, and management.
 * Base path: /api/products
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileStorageService fileStorageService;

    /**
     * GET
     * /api/products?page=0&size=12&sort=createdAt&keyword=...&categoryId=...&locality=...
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) com.campusconnect.market.model.ProductType type,
            @RequestParam(required = false) java.util.List<String> condition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        java.util.List<com.campusconnect.market.model.ProductCondition> conditions = null;
        if (condition != null && !condition.isEmpty()) {
            conditions = condition.stream()
                    .map(c -> com.campusconnect.market.model.ProductCondition.valueOf(c.toUpperCase()))
                    .collect(java.util.stream.Collectors.toList());
        }

        Page<Product> products = productService.searchProducts(keyword, categoryId, location, type, conditions, page,
                size);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /** GET /api/products/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.findById(id);
            return ResponseEntity.ok(ApiResponse.success(product));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/products/student-exchange */
    @GetMapping("/student-exchange")
    public ResponseEntity<ApiResponse<Page<Product>>> getStudentItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.success(productService.getStudentExchangeItems(page, size)));
    }

    /** GET /api/products/pre-orders */
    @GetMapping("/pre-orders")
    public ResponseEntity<ApiResponse<Page<Product>>> getPreOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.success(productService.getPreOrderProducts(page, size)));
    }

    /** POST /api/products — Seller creates a product */
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User seller = userService.findByEmail(userDetails.getUsername());
            Product product = productService.createProduct(request, seller);
            return ResponseEntity.ok(ApiResponse.success(product, "Product listed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST /api/products/upload-image — Upload product image */
    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorageService.storeFile(file);
            return ResponseEntity.ok(ApiResponse.success(url, "Image uploaded"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** PUT /api/products/{id} — Seller updates own product */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User seller = userService.findByEmail(userDetails.getUsername());
            Product product = productService.updateProduct(id, request, seller);
            return ResponseEntity.ok(ApiResponse.success(product, "Product updated"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** DELETE /api/products/{id} — Seller deactivates own product */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User seller = userService.findByEmail(userDetails.getUsername());
            productService.deactivateProduct(id, seller);
            return ResponseEntity.ok(ApiResponse.success("OK", "Product removed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
