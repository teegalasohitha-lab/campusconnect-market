package com.campusconnect.market.service;

import com.campusconnect.market.dto.request.RegisterRequest;
import com.campusconnect.market.model.Role;
import com.campusconnect.market.model.User;
import com.campusconnect.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for user registration and management.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Register a new user (CUSTOMER or SELLER) */
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .phone(request.getPhone())
                .locality(request.getLocality())
                .isActive(true)
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public User updateProfile(Long userId, String name, String phone, String locality) {
        User user = findById(userId);
        if (name != null)
            user.setName(name);
        if (phone != null)
            user.setPhone(phone);
        if (locality != null)
            user.setLocality(locality);
        return userRepository.save(user);
    }

    public void toggleUserStatus(Long userId) {
        User user = findById(userId);
        boolean currentStatus = (user.getIsActive() != null) ? user.getIsActive() : true;
        user.setIsActive(!currentStatus);
        userRepository.save(user);
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
