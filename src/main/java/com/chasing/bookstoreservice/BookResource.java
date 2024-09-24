package com.chasing.bookstoreservice;

import com.chasing.bookstoreservice.entity.Book;
import com.chasing.bookstoreservice.exception.BusinessException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/book")
public class BookResource {

    @Inject
    EntityManager entityManager;

    @GET
    @Path("/list")
    @Produces("application/json")
    public Response list() {
        List<Book> books = entityManager.createQuery("select b from Book b", Book.class).getResultList();
        return Response.ok(books).build();
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Transactional
    public Response addBook(Book book) {
        entityManager.persist(book);
        return Response.ok(book).status(201).build();
    }

    @POST
    @Path("/addStock")
    @Consumes("application/x-www-form-urlencoded")
    @Transactional
    public void addBookWithQuantity(@QueryParam("id") Long id, @QueryParam("quantity") int quantity) {
        Book book = entityManager.find(Book.class, id);
        if (book != null && book.getStock() + quantity >= 0) {
            book.setStock(book.getStock() + quantity);
        } else if (book == null) {
            throw new BusinessException(Response.Status.NOT_FOUND, "No book with id " + id + " exists");
        } else {
            throw new BusinessException(Response.Status.BAD_REQUEST, "Book with id " + id + " has insufficient stock");
        }
    }
}
