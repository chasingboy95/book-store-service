package com.chasing.bookstoreservice.service;

import com.chasing.bookstoreservice.entity.Book;
import com.chasing.bookstoreservice.exception.BusinessException;
import com.chasing.bookstoreservice.repository.BookRepository;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    public void updateStock(Long bookId,
                            @Parameter(description = "The amount of stock you want to add (positive integer) / decrease (negative integer)") int stock) {
        var book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            int updated = bookRepository.increaseStockWithQuantity(bookId, stock);
            if (updated < 1) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Modified stock cannot be less than 0");
            }
        } else {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }
}
