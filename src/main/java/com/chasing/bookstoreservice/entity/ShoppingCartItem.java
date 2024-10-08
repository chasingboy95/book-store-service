package com.chasing.bookstoreservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "shopping_cart")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartItem {
    @Id
    @GeneratedValue(generator = "auto")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "quantity")
    private int quantity;
}
