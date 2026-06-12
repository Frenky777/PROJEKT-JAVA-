package com.example.demo.controllers;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    BookService bookService;
    @InjectMocks
    BookController bookController;

    @Test
    void getAllBooks_returnsOkWithList() {
        Book book = new Book();
        when(bookService.getAllBooks()).thenReturn(List.of(book));

        ResponseEntity<List<Book>> response = bookController.getAllBooks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(book);
    }

    @Test
    void addBook_returnsOkWithSavedBook() {
        Book book = new Book();
        when(bookService.addBook(book)).thenReturn(book);

        ResponseEntity<Book> response = bookController.addBook(book);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(book);
    }
}
