package com.library.library.service.impl;

import com.library.library.dto.AuthorBookDto;
import com.library.library.dto.AuthorDto;
import com.library.library.dao.entity.Author;
import com.library.library.dao.entity.Book;
import com.library.library.exception.NotFoundException;
import com.library.library.mapper.AuthorMapper;
import com.library.library.dao.repository.AuthorRepository;
import com.library.library.dao.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Автор не найден: " + id));
    }

    public AuthorDto findByIdDto(Long id) {
        return AuthorMapper.toDto(findById(id));
    }

    public List<AuthorDto> searchByName(String name) {
        return authorRepository.findByFullNameContainingIgnoreCase(name).stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public AuthorDto create(AuthorDto dto) {
        Author author = AuthorMapper.toEntity(dto);
        author = authorRepository.save(author);

        if (dto.books() != null) {
            for (AuthorBookDto bookDto : dto.books()) {
                if (!StringUtils.hasText(bookDto.title())) {
                    continue;
                }
                // Если такая книга (название + год) уже есть — не создаём дубль,
                // а просто привязываем к ней нового автора.
                Book book = bookRepository.findFirstByTitleIgnoreCaseAndPublishYear(bookDto.title(), bookDto.publishYear())
                        .orElseGet(Book::new);
                book.setTitle(bookDto.title());
                book.setPublishYear(bookDto.publishYear());
                if (book.getGenre() == null) {
                    book.setGenre(bookDto.genre());
                }
                book.getAuthors().add(author);
                bookRepository.save(book);
            }
        }

        return AuthorMapper.toDto(author);
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public AuthorDto update(Long id, AuthorDto dto) {
        Author author = findById(id);
        author.setFullName(dto.fullName());
        author.setBirthYear(dto.birthYear());
        author.setBiography(dto.biography());
        return AuthorMapper.toDto(authorRepository.save(author));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public void delete(Long id) {
        Author author = findById(id);
        // Сначала отвязываем автора от книг (записи в book_authors),
        // иначе удаление упадёт на FK-constraint.
        for (Book book : author.getBooks()) {
            book.getAuthors().remove(author);
        }
        authorRepository.delete(author);
    }
}
