package com.chasing.bookstoreservice.repository;

import com.chasing.bookstoreservice.entity.ShoppingCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCartItem, Long> {
    Optional<ShoppingCartItem> findByUserIdAndBookId(Long userId, Long bookId);

    Optional<List<ShoppingCartItem>> findByUserId(Long userId);

    @Transactional
    void deleteByUserIdAndBookId(Long userId, Long bookId);
}
