package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book addBook(Book bookRequest) {
        Book newBook = new Book.Builder()
                .title(bookRequest.getTitle())
                .author(bookRequest.getAuthor())
                .isbn(bookRequest.getIsbn())
                .quantity(bookRequest.getQuantity())
                .build();

        return bookRepository.save(newBook);
    }
    // aktualizacja książki
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono książki o ID: " + id));

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setQuantity(bookDetails.getQuantity());

        return bookRepository.save(book);
    }

    // usuwanie książki
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono książki o ID: " + id));
        bookRepository.delete(book);
    }
}