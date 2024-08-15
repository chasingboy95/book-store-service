package com.chasing.bookstoreservice.controller;

import com.chasing.bookstoreservice.entity.Book;
import com.chasing.bookstoreservice.entity.Order;
import com.chasing.bookstoreservice.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ShoppingCartControllerTest {
    ObjectMapper om = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DirtiesContext
    void addBookToCart() throws Exception {
        Book book = Book.builder().id(1L).title("book1").author("author1").price(29.99).stock(1).build();
        bookRepository.save(book);
        mockMvc.perform(post("/cart")
                        .param("userId", "1")
                        .param("bookId", "1"))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DirtiesContext
    void addBookToCartBookNotFound() throws Exception {
        mockMvc.perform(post("/cart")
                        .param("userId", "1")
                        .param("bookId", "1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void listCartItems() throws Exception {
        addBookToCart();
        addBookToCart();
        mockMvc.perform(get("/cart/list")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> Assert.isTrue(om.readValue(result.getResponse().getContentAsString(), List.class).size() == 1, "Add book1 2 times, but list only 1 item"))
                .andExpect(result -> Assert.isTrue(result.getResponse().getContentAsString().contains("\"quantity\":2"), "book quantity should be 2"));

    }

    @Test
    @DirtiesContext
    void deleteBookFromCart() throws Exception {
        addBookToCart();
        mockMvc.perform(delete("/cart")
                        .param("userId", "1")
                        .param("bookId", "1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DirtiesContext
    void checkout() throws Exception {
        addBookToCart();
        mockMvc.perform(post("/cart/checkout")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> Assert.isTrue(om.readValue(result.getResponse().getContentAsString(), Order.class).getTotalPrice() == 29.99, "Book total price should be 29.99"));

    }

    @Test
    @DirtiesContext
    void checkoutNoItemInCart() throws Exception {
        mockMvc.perform(post("/cart/checkout")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void checkoutOutOfStock() throws Exception {
        addBookToCart();
        addBookToCart();
        mockMvc.perform(post("/cart/checkout")
                        .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assert.isTrue(result.getResponse().getContentAsString().contains("Insufficient stock of books: book1"), "Book1 is out of stock"));
    }

}
