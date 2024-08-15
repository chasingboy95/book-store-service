package com.chasing.bookstoreservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(generator = "auto")
    private Long id;

    @Column(name = "title")
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Column(name = "author")
    @NotBlank(message = "Author is mandatory")
    private String author;

    @Column(name = "price")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;

    @Column(name = "category")
    private Category category;

    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    @Column(name = "stock")
    private int stock;
}
