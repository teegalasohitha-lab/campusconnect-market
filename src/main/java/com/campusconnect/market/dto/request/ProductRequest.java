package com.campusconnect.market.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private Long categoryId;
    private String location;
    private String type; // NORMAL, EXCHANGE, PREORDER
    private String condition;

    /** Path/URL of the uploaded image */
    private String imageUrl;

    private LocalDate preOrderDeadline;
    private Integer minPreOrderQty = 1;
}
