package com.library.library.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AuthorDto(
        Long id,

        @NotBlank(message = "ФИО автора не может быть пустым")
        @Size(min = 2, max = 100, message = "ФИО должно быть от 2 до 100 символов")
        String fullName,

        @NotNull(message = "Год рождения обязателен")
        @Min(value = 1000, message = "Год рождения не может быть раньше 1000")
        @Max(value = 2025, message = "Год рождения должен быть в прошлом")
        Integer birthYear,

        @Size(max = 1000, message = "Биография не может быть длиннее 1000 символов")
        String biography,

        // Произведения, добавляемые вместе с автором (форма добавления автора).
        // Пустые строки игнорируются при сохранении.
        @Valid
        List<AuthorBookDto> books
) {}
