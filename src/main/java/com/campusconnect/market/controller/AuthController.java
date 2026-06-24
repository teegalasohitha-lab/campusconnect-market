package com.campusconnect.market.controller;

import com.campusconnect.market.dto.request.LoginRequest;
import com.campusconnect.market.dto.request.RegisterRequest;
import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.dto.response.JwtResponse;
import com.campusconnect.market.model.User;
import com.campusconnect.market.security.JwtTokenProvider;
import com.campusconnect.market.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for registration and login.
 * Base path: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private UserService userService;

    /** POST /api/auth/register */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request);
            return ResponseEntity.ok(ApiResponse.success(
                    "Registration successful", "Welcome to CampusConnect, " + user.getName() + "!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /** POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            User user = userService.findByEmail(request.getEmail());

            return ResponseEntity.ok(new JwtResponse(
                    jwt, user.getId(), user.getName(), user.getEmail(), user.getRole().name()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid email or password"));
        } catch (DisabledException e) {
            return ResponseEntity.status(403).body(ApiResponse.error("Account is disabled"));
        }
    }

    /** GET /api/auth/profile */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /** PUT /api/auth/profile */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @RequestBody java.util.Map<String, String> body,
            Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        User updated = userService.updateProfile(user.getId(), body.get("name"), body.get("phone"),
                body.get("locality"));
        return ResponseEntity.ok(ApiResponse.success(updated, "Profile updated successfully"));
    }
}
