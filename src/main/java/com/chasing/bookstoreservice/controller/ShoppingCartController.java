package com.chasing.bookstoreservice.controller;

import com.chasing.bookstoreservice.entity.Order;
import com.chasing.bookstoreservice.entity.ShoppingCartItemVo;
import com.chasing.bookstoreservice.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping
    public ResponseEntity<Void> addBookToCart(Long userId, Long bookId) {
        shoppingCartService.addBookToCart(userId, bookId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ShoppingCartItemVo>> listCartItems(Long userId) {
        return ResponseEntity.of(Optional.ofNullable(shoppingCartService.getCartItems(userId)));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeBookFromCart(Long userId, Long bookId) {
        shoppingCartService.removeBookFromCart(userId, bookId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(Long userId, @RequestParam(required = false, defaultValue = "false") boolean deleteFromCart) {
        Order order = shoppingCartService.checkout(userId, deleteFromCart);
        return ResponseEntity.of(Optional.of(order));
    }

}
