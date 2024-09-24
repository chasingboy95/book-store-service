package com.chasing.bookstoreservice;

import com.chasing.bookstoreservice.entity.Book;
import com.chasing.bookstoreservice.entity.Order;
import com.chasing.bookstoreservice.entity.ShoppingCartItem;
import com.chasing.bookstoreservice.entity.ShoppingCartItemVo;
import com.chasing.bookstoreservice.exception.BusinessException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Path("/cart")
public class ShoppingCartResource {

    @Inject
    EntityManager entityManager;

    @POST
    @Produces("application/json")
    @Transactional
    @SuppressWarnings("unchecked")
    public void addBookToCart(@QueryParam("userId") Long userId, @QueryParam("bookId") Long bookId) {
        Book book = entityManager.find(Book.class, bookId);
        if (book == null) {
            throw new BusinessException(Response.Status.NOT_FOUND, "No book with id " + bookId + " exists");
        }
        Query query = entityManager.createQuery("SELECT c FROM ShoppingCartItem c WHERE c.userId = :userId AND c.bookId = :bookId");
        query.setParameter("userId", userId);
        query.setParameter("bookId", bookId);
        List<ShoppingCartItem> cartItems = (List<ShoppingCartItem>) query.getResultList();
        if(!cartItems.isEmpty()) {
            ShoppingCartItem cartItem = cartItems.getFirst();
            cartItem.setQuantity(cartItem.getQuantity()+1);
        } else {
            var cartItem = ShoppingCartItem.builder().userId(userId).bookId(bookId).quantity(1).build();
            entityManager.persist(cartItem);
        }
    }

    @GET
    @Path("/list")
    @Produces("application/json")
    @SuppressWarnings("unchecked")
    public List<ShoppingCartItem> listCartItems(@QueryParam("userId") Long userId) {
        Query query = entityManager.createQuery("SELECT c FROM ShoppingCartItem c WHERE c.userId = :userId");
        query.setParameter("userId", userId);
        return (List<ShoppingCartItem>) query.getResultList();
    }

    @DELETE
    @Transactional
    public void removeBookFromCart(@QueryParam("userId") Long userId, @QueryParam("bookId") Long bookId) {
        Query query = entityManager.createQuery("DELETE FROM ShoppingCartItem c WHERE c.userId = :userId AND c.bookId = :bookId");
        query.setParameter("userId", userId);
        query.setParameter("bookId", bookId);
        query.executeUpdate();
    }

    @POST
    @Path("/checkout")
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    @Transactional
    @SuppressWarnings("unchecked")
    public Order checkout(@QueryParam("userId") Long userId, @QueryParam("deleteFromCart") boolean deleteFromCart) {
        AtomicReference<Double> totalPrice = new AtomicReference<>((double) 0);
        Query query = entityManager.createQuery("SELECT c FROM ShoppingCartItem c WHERE c.userId = :userId");
        query.setParameter("userId", userId);
        List<ShoppingCartItem> cartItems = (List<ShoppingCartItem>) query.getResultList();
        if (cartItems.isEmpty()){
            throw new BusinessException(Response.Status.NOT_FOUND, "no cart items found");
        }
        List<ShoppingCartItemVo> cartItemVos = new ArrayList<>();
        for (ShoppingCartItem item : cartItems) {
            Long bookId = item.getBookId();
            Book book = entityManager.find(Book.class, bookId);
            if (book != null) {
                if (book.getStock() < item.getQuantity()) {
                    throw new BusinessException(Response.Status.BAD_REQUEST, "Insufficient stock of books: " + book.getTitle());
                }
                book.setStock(book.getStock() - item.getQuantity());
                totalPrice.updateAndGet(v -> v + book.getPrice() * item.getQuantity());
                cartItemVos.add(ShoppingCartItemVo.builder()
                        .bookId(bookId)
                        .quantity(item.getQuantity())
                        .title(book.getTitle())
                        .price(book.getPrice())
                        .build());
                if (deleteFromCart) {
                    entityManager.remove(item);
                }
            }
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
