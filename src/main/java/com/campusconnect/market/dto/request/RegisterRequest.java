package com.campusconnect.market.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /** CUSTOMER or SELLER */
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "CUSTOMER|SELLER", message = "Role must be CUSTOMER or SELLER")
    private String role;

    private String phone;
    private String locality;
}
