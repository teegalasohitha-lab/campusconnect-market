package com.campusconnect.market.repository;

import com.campusconnect.market.model.Product;
import com.campusconnect.market.model.ProductCondition;
import com.campusconnect.market.model.ProductType;
import com.campusconnect.market.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

       // ── Seller queries ────────────────────────────────────────────
       List<Product> findByTitleContainingIgnoreCase(String keyword);

       List<Product> findBySeller(User seller);

       List<Product> findBySellerAndIsActive(User seller, Boolean isActive);

       long countBySeller(User seller);

       // ── Active product listings ───────────────────────────────────
       Page<Product> findByIsActive(Boolean isActive, Pageable pageable);

       // ── Search by keyword ────────────────────────────────────────
       @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
                     "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     " LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
       Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

       // ── Filter by category ───────────────────────────────────────
       @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category.id = :categoryId")
       Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

       // ── Nearby / location ────────────────────────────────────────
       @Query("SELECT p FROM Product p WHERE p.isActive = true AND LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))")
       Page<Product> findByLocation(@Param("location") String location, Pageable pageable);

       // ── Specific Type queries ─────────────────────────────────────
       Page<Product> findByTypeAndIsActive(ProductType type, Boolean isActive, Pageable pageable);

       // ── Stats ───────────────────────────────────────────────────
       long countByIsActive(Boolean isActive);

       // ── Combined search + filter ─────────────────────────────────
       @Query("SELECT p FROM Product p WHERE p.isActive = true " +
                     "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
                     +
                     "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
                     "AND (:location IS NULL OR :location = '' OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) "
                     +
                     "AND (:type IS NULL OR p.type = :type) " +
                     "AND (:conditions IS NULL OR p.condition IN :conditions)")
       Page<Product> findWithFilters(@Param("keyword") String keyword,
                     @Param("categoryId") Long categoryId,
                     @Param("location") String location,
                     @Param("type") ProductType type,
                     @Param("conditions") Collection<ProductCondition> conditions,
                     Pageable pageable);
}
