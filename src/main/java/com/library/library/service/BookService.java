package com.library.library.service;

import com.library.library.dto.BookDto;
import com.library.library.entity.Author;
import com.library.library.entity.Book;
import com.library.library.entity.User;
import com.library.library.exception.ConflictException;
import com.library.library.exception.NotFoundException;
import com.library.library.mapper.BookMapper;
import com.library.library.repository.AuthorRepository;
import com.library.library.repository.BookRepository;
import com.library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserRepository userRepository;

    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toDto)
                .toList();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книга не найдена: " + id));
    }

    public BookDto findByIdDto(Long id) {
        return BookMapper.toDto(findById(id));
    }

    public List<BookDto> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(BookMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public BookDto create(BookDto dto) {
        Book book = BookMapper.toEntity(dto);
        return BookMapper.toDto(bookRepository.save(book));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public BookDto update(Long id, BookDto dto) {
        Book book = findById(id);
        book.setTitle(dto.title());
        book.setPublishYear(dto.publishYear());
        book.setGenre(dto.genre());
        return BookMapper.toDto(bookRepository.save(book));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public void delete(Long id) {
        findById(id);
        bookRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public BookDto addAuthor(Long bookId, Long authorId) {
        Book book = findById(bookId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Автор не найден: " + authorId));
        book.getAuthors().add(author);
        return BookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto takeBook(Long bookId, Long userId) {
        Book book = findById(bookId);
        if (book.getTakenByUser() != null) {
            throw new ConflictException("Книга уже занята");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        book.setTakenByUser(user);
        book.setTakenAt(LocalDateTime.now());
        return BookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto returnBook(Long bookId) {
        Book book = findById(bookId);
        if (book.getTakenByUser() == null) {
            throw new ConflictException("Книга и так свободна");
        }
        book.setTakenByUser(null);
        book.setTakenAt(null);
        return BookMapper.toDto(bookRepository.save(book));
    }
}
