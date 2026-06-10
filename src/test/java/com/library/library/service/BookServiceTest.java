package com.library.library.service;

import com.library.library.dto.BookDto;
import com.library.library.entity.Book;
import com.library.library.entity.User;
import com.library.library.exception.ConflictException;
import com.library.library.exception.NotFoundException;
import com.library.library.repository.AuthorRepository;
import com.library.library.repository.BookRepository;
import com.library.library.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private User user;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Война и мир");
        book.setPublishYear(1869);
        book.setGenre("Роман");

        user = new User();
        user.setId(2L);
    }

    @Test
    void findById_throwsNotFound_whenBookMissing() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_savesBook() {
        BookDto dto = new BookDto(null, "Война и мир", 1869, "Роман", null, null, null);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto result = bookService.create(dto);

        assertThat(result.title()).isEqualTo("Война и мир");
        assertThat(result.publishYear()).isEqualTo(1869);
    }

    @Test
    void takeBook_marksBookAsTaken_whenAvailable() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookDto result = bookService.takeBook(1L, 2L);

        assertThat(result.takenByUserId()).isEqualTo(2L);
        assertThat(result.takenAt()).isNotNull();
    }

    @Test
    void takeBook_throwsConflict_whenAlreadyTaken() {
        book.setTakenByUser(user);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.takeBook(1L, 2L))
                .isInstanceOf(ConflictException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void returnBook_clearsTakenInfo_whenBookIsTaken() {
        book.setTakenByUser(user);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        BookDto result = bookService.returnBook(1L);

        assertThat(result.takenByUserId()).isNull();
        assertThat(result.takenAt()).isNull();
    }

    @Test
    void returnBook_throwsConflict_whenBookAlreadyFree() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.returnBook(1L))
                .isInstanceOf(ConflictException.class);
    }
}
