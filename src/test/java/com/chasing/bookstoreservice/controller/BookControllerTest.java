package com.chasing.bookstoreservice.controller;

import com.chasing.bookstoreservice.entity.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BookControllerTest {

    ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    void addBook() throws Exception {
        Book book = Book.builder()
                .title("book1")
                .author("author1")
                .stock(1)
                .price(12.99)
                .build();
        Book actual = om.readValue(mockMvc.perform(post("/book")
                        .contentType("application/json")
                        .content(om.writeValueAsString(book)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), Book.class);
        Assertions.assertEquals("book1", actual.getTitle());
        Assertions.assertNotNull(actual.getId());
    }

    @Test
    @DirtiesContext
    void addStock() throws Exception {
        addBook();
        mockMvc.perform(post("/book/addStock")
                        .contentType("application/json")
                        .param("id", "1")
                        .param("stock", "10"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DirtiesContext
    void addStockInvalid() throws Exception {
        addBook();
        mockMvc.perform(post("/book/addStock")
                        .contentType("application/json")
                        .param("id", "1")
                        .param("stock", "-2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assert.isTrue(result.getResponse().getContentAsString().equals("Modified stock cannot be less than 0"), "Invalid stock message"));
    }

    @Test
    @DirtiesContext
    void addStockNotFound() throws Exception {
        mockMvc.perform(post("/book/addStock")
                        .contentType("application/json")
                        .param("id", "1")
                        .param("stock", "10"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assert.isTrue(result.getResponse().getContentAsString().equals("Book not found"), "Book not found message"));
    }

    @Test
    @DirtiesContext
    void listBooks() throws Exception {
        addBook();
        addBook();
        mockMvc.perform(get("/book/list")
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> Assert.isTrue(om.readValue(result.getResponse().getContentAsString(), List.class).size() == 2, "Book list contains book1"));
    }
}
