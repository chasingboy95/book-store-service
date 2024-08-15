package com.chasing.bookstoreservice.service;

import com.chasing.bookstoreservice.entity.Book;
import com.chasing.bookstoreservice.entity.Order;
import com.chasing.bookstoreservice.entity.ShoppingCartItem;
import com.chasing.bookstoreservice.entity.ShoppingCartItemVo;
import com.chasing.bookstoreservice.exception.BusinessException;
import com.chasing.bookstoreservice.repository.BookRepository;
import com.chasing.bookstoreservice.repository.ShoppingCartRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, BookRepository bookRepository) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void addBookToCart(Long userId, Long bookId) {
        if (bookRepository.findById(bookId).isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Book id=" + bookId + " not found");
        }
        Optional<ShoppingCartItem> optionalCartItem = shoppingCartRepository.findByUserIdAndBookId(userId, bookId);
        if (optionalCartItem.isPresent()) {
            ShoppingCartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            shoppingCartRepository.save(cartItem);
        } else {
            shoppingCartRepository.save(ShoppingCartItem.builder()
                    .userId(userId)
                    .bookId(bookId)
                    .quantity(1)
                    .build());
        }
    }

    public void removeBookFromCart(Long userId, Long bookId) {
        shoppingCartRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    public List<ShoppingCartItemVo> getCartItems(Long userId) {
        return shoppingCartRepository.findByUserId(userId).orElse(new ArrayList<>())
                .stream().map(item -> {
                    Book book = bookRepository.findById(item.getBookId()).get();
                    ShoppingCartItemVo cartItemVo = new ShoppingCartItemVo();
                    BeanUtils.copyProperties(book, cartItemVo);
                    cartItemVo.setQuantity(item.getQuantity());
                    return cartItemVo;
                }).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public Order checkout(Long userId, boolean deleteFromCart) {
        AtomicReference<Double> totalPrice = new AtomicReference<>((double) 0);
        Optional<List<ShoppingCartItem>> optionalCartItems = shoppingCartRepository.findByUserId(userId);
        if (optionalCartItems.isEmpty() || optionalCartItems.get().isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "No cart items found");
        }
        List<ShoppingCartItemVo> cartItemVos = new ArrayList<>();
        for (ShoppingCartItem item : optionalCartItems.get()) {
            Long bookId = item.getBookId();
            bookRepository.findById(bookId).ifPresent(book -> {
                int updates = bookRepository.increaseStockWithQuantity(bookId, item.getQuantity() * (-1));
                if (updates < 1) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "Insufficient stock of books: " + book.getTitle());
                }
                totalPrice.updateAndGet(v -> v + book.getPrice() * item.getQuantity());
                cartItemVos.add(ShoppingCartItemVo.builder()
                        .bookId(bookId)
                        .quantity(item.getQuantity())
                        .title(book.getTitle())
                        .price(book.getPrice())
                        .build());
                if (deleteFromCart) {
                    shoppingCartRepository.deleteByUserIdAndBookId(userId, bookId);
                }
            });
        }
        return Order.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .userId(userId)
                .books(cartItemVos)
                .totalPrice(totalPrice.get())
                .createTime(LocalDateTime.now())
                .build();
    }
}
