package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookService bookService;

    @Test
    void getAllBooks_returnsRepositoryResult() {
        Book book = new Book();
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookService.getAllBooks();

        assertThat(result).containsExactly(book);
        verify(bookRepository).findAll();
    }

    @Test
    void addBook_savesAndReturnsBook() {
        Book book = new Book();
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.addBook(book);

        assertThat(result).isSameAs(book);
        verify(bookRepository).save(book);
    }
}
