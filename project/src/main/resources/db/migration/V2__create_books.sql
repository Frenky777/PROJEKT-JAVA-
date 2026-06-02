
CREATE TABLE books (
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    author     VARCHAR(255) NOT NULL,
    isbn       VARCHAR(100) NOT NULL UNIQUE,
    quantity   INT NOT NULL
);
