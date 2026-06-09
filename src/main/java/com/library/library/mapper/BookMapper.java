package com.library.library.mapper;

import com.library.library.dto.AuthorDto;
import com.library.library.dto.BookDto;
import com.library.library.entity.Book;

import java.util.Set;
import java.util.stream.Collectors;

public class BookMapper {
    private BookMapper() {}

    public static BookDto toDto(Book b) {
        Set<AuthorDto> authors = b.getAuthors() == null ? Set.of() :
                b.getAuthors().stream()
                        .map(AuthorMapper::toDto)
                        .collect(Collectors.toSet());

        return new BookDto(
                b.getId(),
                b.getTitle(),
                b.getPublishYear(),
                b.getGenre(),
                authors,
                b.getTakenByUser() != null ? b.getTakenByUser().getId() : null,
                b.getTakenAt()
        );
    }

    public static Book toEntity(BookDto dto) {
        Book b = new Book();
        b.setTitle(dto.title());
        b.setPublishYear(dto.publishYear());
        b.setGenre(dto.genre());
        return b;
    }
}
