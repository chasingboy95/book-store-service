package com.chasing.bookstoreservice.controller;

import com.chasing.bookstoreservice.entity.Book;
import com.chasing.bookstoreservice.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @Operation(summary = "Add book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(examples = {
                            @ExampleObject(name = "{\"id\":2,\"title\":\"Pride and Prejudice\",\"author\":\"Pride and Prejudice\",\"price\":22,\"category\":\"LITERATURE\",\"stock\":12}"),
                    })
            })
    })
    public ResponseEntity<Book> save(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(bookService.addBook(book), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    @Operation(summary = "List all books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {
                    @Content(examples = {
                            @ExampleObject(name = "[{\"id\":2,\"title\":\"Pride and Prejudice\",\"author\":\"Pride and Prejudice\",\"price\":22,\"category\":\"LITERATURE\",\"stock\":12}, ...]"),
                    })
            })
    })
    public ResponseEntity<List<Book>> list() {
        return ResponseEntity.of(Optional.ofNullable(bookService.findAll()));
    }

    @PostMapping("/addStock")
    @Operation(summary = "Increase or decrease stock of a book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Invalid stock", content = {
                    @Content(examples = {
                            @ExampleObject(name = "Modified stock cannot be less than 0"),
                    })
            }),
            @ApiResponse(responseCode = "404", description = "bookId not found", content = {
                    @Content(examples = {
                            @ExampleObject(name = "Book not found"),
                    })
            }),
    })
    public ResponseEntity<Void> addStock(Long id, int stock) {
        bookService.updateStock(id, stock);
        return ResponseEntity.ok().build();
    }
}
