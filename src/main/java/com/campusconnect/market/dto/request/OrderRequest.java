package com.campusconnect.market.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    private String paymentMethod = "Cash on Delivery";
}
