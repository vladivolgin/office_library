package com.library.library.service.impl;

import com.library.library.dto.BookDto;
import com.library.library.dao.entity.Author;
import com.library.library.dao.entity.Book;
import com.library.library.dao.entity.User;
import com.library.library.exception.ConflictException;
import com.library.library.exception.ForbiddenException;
import com.library.library.exception.NotFoundException;
import com.library.library.mapper.BookMapper;
import com.library.library.dao.repository.AuthorRepository;
import com.library.library.dao.repository.BookRepository;
import com.library.library.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
    public BookDto takeBook(Long bookId, Authentication authentication) {
        Book book = findById(bookId);
        if (book.getTakenByUser() != null) {
            throw new ConflictException("Книга уже занята");
        }
        User user = currentUser(authentication);
        book.setTakenByUser(user);
        book.setTakenAt(LocalDateTime.now());
        return BookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto returnBook(Long bookId, Authentication authentication) {
        Book book = findById(bookId);
        if (book.getTakenByUser() == null) {
            throw new ConflictException("Книга и так свободна");
        }
        User user = currentUser(authentication);
        boolean isOwner = book.getTakenByUser().getId().equals(user.getId());
        boolean isEditor = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_EDITOR"::equals);
        if (!isOwner && !isEditor) {
            throw new ForbiddenException("Книга взята другим пользователем");
        }
        book.setTakenByUser(null);
        book.setTakenAt(null);
        return BookMapper.toDto(bookRepository.save(book));
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + authentication.getName()));
    }
}
