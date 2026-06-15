package com.library.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
        @Min(value = 1000, message = "Год публикации не может быть раньше 1000")
        @Max(value = 2025, message = "Год публикации не может быть в будущем")
        Integer publishYear,

        @NotBlank(message = "Жанр не может быть пустым")
        @Size(max = 50, message = "Жанр не может быть длиннее 50 символов")
        String genre,

        Set<AuthorDto> authors,
        Long takenByUserId,
        LocalDateTime takenAt,
        long loanCount
) {}
