package com.campusconnect.market.controller;

import com.campusconnect.market.dto.response.ApiResponse;
import com.campusconnect.market.model.Category;
import com.campusconnect.market.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.findAll()));
    }

    @GetMapping("/top-level")
    public ResponseEntity<ApiResponse<List<Category>>> getTopLevel() {
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.findByParentIsNull()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody Category category) {
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.save(category), "Category created"));
    }
}
