package com.chasing.bookstoreservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemVo {
    private Long bookId;
    private String title;
    private double price;
    private int quantity;
}
