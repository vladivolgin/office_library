package com.library.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

public record BookDto(
        Long id,

        @NotBlank(message = "Название книги не может быть пустым")
        @Size(min = 1, max = 200, message = "Название должно быть от 1 до 200 символов")
        String title,

        @NotNull(message = "Год публикации обязателен")
        Integer publishYear,

        @NotBlank(message = "Жанр не может быть пустым")
        @Size(max = 50, message = "Жанр не может быть длиннее 50 символов")
        String genre,

        Set<AuthorDto> authors,
        Long takenByUserId,
        LocalDateTime takenAt
) {}
