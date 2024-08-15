# Getting Started

## Project Introduction
- **Framework:** SpringBoot, I chose SpringBoot because of its powerful ecosystem, which I can quickly utilize for development tasks
- **Database:** H2
- **API Documentation:** Swagger [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Environment
- Java version: 17
- Maven version: 3.*
- Spring Boot version: 3.3.2

## Data

Example of a Book data JSON object:

```json
{
  "title": "Modern Java in Action",
  "author": "Raoul-Gabriel Urma",
  "price": 19.99,
  "category": "SCIENCE_AND_TECHNOLOGY",
  "stock": 10
}
```

Example of a ShoppingCart data :
`userId` any long value, 1,2,3... etc


## Commands

- run:

```bash
mvn clean spring-boot:run
```

- install:

```bash
mvn clean install
```

- test:

```bash
mvn clean test
```

## Design
Bookstore has two main entities, `Book` and `ShoppingCartItem`.
- `Book` entity represents the book details, which has the following fields:
  - `title`: title of the book
  - `author`: author of the book
  - `price`: price of the book
  - `category`: category of the book
  - `stock`: stock of the book
- `ShoppingCartItem` entity represents the shopping cart item, which has the following fields:
  - `userId`: user id of the shopping cart item
  - `bookId`: book id of the shopping cart item
  - `quantity`: quantity of the book in the shopping cart item
one book can be added to the shopping cart multiple times, and also can be add to the shopping cart by multiple users
one shopping cart can have multiple books, each one is a shopping cart item
one user can have multiple shopping cart items, but one shopping cart item can only belong to one user
