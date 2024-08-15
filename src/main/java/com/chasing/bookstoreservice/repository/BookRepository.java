package com.chasing.bookstoreservice.repository;

import com.chasing.bookstoreservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "update BOOK set STOCK=STOCK-?3 where userId=$1 and bookId=?2 and STOCK>?3", nativeQuery = true)
    @Modifying
    @Transactional
    int reduceStock(Long userId, Long bookId, int quantity);

    @Query(value = "update BOOK set STOCK=STOCK+?2 where id=?1 and (STOCK+?2)>=0", nativeQuery = true)
    @Modifying
    @Transactional
    int increaseStockWithQuantity(Long bookId, int quantity);
}
