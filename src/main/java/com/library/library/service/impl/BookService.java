package com.library.library.service.impl;

import com.library.library.dto.BookDto;
import com.library.library.dto.LoanDto;
import com.library.library.dao.entity.Author;
import com.library.library.dao.entity.Book;
import com.library.library.dao.entity.BookLoan;
import com.library.library.dao.entity.User;
import com.library.library.exception.ConflictException;
import com.library.library.exception.ForbiddenException;
import com.library.library.exception.NotFoundException;
import com.library.library.mapper.BookMapper;
import com.library.library.dao.repository.AuthorRepository;
import com.library.library.dao.repository.BookLoanRepository;
import com.library.library.dao.repository.BookRepository;
import com.library.library.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookLoanRepository bookLoanRepository;
    private final UserRepository userRepository;

    // Книги сортируются по популярности: сначала те, которые чаще брали в выдачу
    public List<BookDto> findAll() {
        Map<Long, Long> loanCounts = new HashMap<>();
        for (Object[] row : bookLoanRepository.countLoansGroupedByBookId()) {
            loanCounts.put((Long) row[0], (Long) row[1]);
        }
        return bookRepository.findAll().stream()
                .sorted(Comparator.comparingLong((Book b) -> loanCounts.getOrDefault(b.getId(), 0L)).reversed())
                .map(b -> BookMapper.toDto(b, loanCounts.getOrDefault(b.getId(), 0L)))
                .toList();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Книга не найдена: " + id));
    }

    public BookDto findByIdDto(Long id) {
        return BookMapper.toDto(findById(id));
    }

    public List<LoanDto> findLoanHistory(Long bookId) {
        return bookLoanRepository.findByBookIdOrderByTakenAtDesc(bookId).stream()
                .map(loan -> new LoanDto(
                        loan.getId(),
                        loan.getUser().getFullName(),
                        loan.getTakenAt(),
                        loan.getReturnedAt()
                ))
                .toList();
    }

    public List<BookDto> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(BookMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public BookDto create(BookDto dto) {
        bookRepository.findFirstByTitleIgnoreCaseAndPublishYear(dto.title(), dto.publishYear())
                .ifPresent(b -> {
                    throw new ConflictException("Книга «" + dto.title() + "» (" + dto.publishYear() + ") уже существует");
                });
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

    // Полностью заменяет набор авторов книги (используется формой редактирования)
    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public BookDto updateAuthors(Long id, List<Long> authorIds) {
        Book book = findById(id);
        Set<Author> authors = authorIds == null || authorIds.isEmpty()
                ? new HashSet<>()
                : new HashSet<>(authorRepository.findAllById(authorIds));
        book.setAuthors(authors);
        return BookMapper.toDto(bookRepository.save(book));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        // Очищаем связи с авторами (book_authors), иначе удаление упадёт на FK-constraint.
        book.getAuthors().clear();
        bookRepository.delete(book);
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
        User user = (User) authentication.getPrincipal();
        LocalDateTime now = LocalDateTime.now();
        book.setTakenByUser(user);
        book.setTakenAt(now);

        BookLoan loan = new BookLoan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setTakenAt(now);
        bookLoanRepository.save(loan);

        return BookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public BookDto returnBook(Long bookId, Authentication authentication) {
        Book book = findById(bookId);
        if (book.getTakenByUser() == null) {
            throw new ConflictException("Книга и так свободна");
        }
        User user = (User) authentication.getPrincipal();
        boolean isOwner = book.getTakenByUser().getId().equals(user.getId());
        boolean isEditor = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_EDITOR"::equals);
        if (!isOwner && !isEditor) {
            throw new ForbiddenException("Книга взята другим пользователем");
        }
        book.setTakenByUser(null);
        book.setTakenAt(null);

        bookLoanRepository.findFirstByBookIdAndReturnedAtIsNull(bookId)
                .ifPresent(loan -> loan.setReturnedAt(LocalDateTime.now()));

        return BookMapper.toDto(bookRepository.save(book));
    }

    // EDITOR может вручную отметить книгу как занятую (выбрав читателя) или свободную
    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public BookDto setAvailability(Long bookId, Long userId) {
        Book book = findById(bookId);
        LocalDateTime now = LocalDateTime.now();

        if (book.getTakenByUser() != null) {
            bookLoanRepository.findFirstByBookIdAndReturnedAtIsNull(bookId)
                    .ifPresent(loan -> loan.setReturnedAt(now));
            book.setTakenByUser(null);
            book.setTakenAt(null);
        }

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
            book.setTakenByUser(user);
            book.setTakenAt(now);

            BookLoan loan = new BookLoan();
            loan.setBook(book);
            loan.setUser(user);
            loan.setTakenAt(now);
            bookLoanRepository.save(loan);
        }

        return BookMapper.toDto(bookRepository.save(book));
    }
}
