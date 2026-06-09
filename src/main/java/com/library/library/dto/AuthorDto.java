package com.library.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.Year;

public record AuthorDto(
        Long id,

        @NotBlank(message = "ФИО автора не может быть пустым")
        @Size(min = 2, max = 100, message = "ФИО должно быть от 2 до 100 символов")
        String fullName,

        @NotNull(message = "Год рождения обязателен")
        @Past(message = "Год рождения должен быть в прошлом")
        Integer birthYear,

        @Size(max = 1000, message = "Биография не может быть длиннее 1000 символов")
        String biography
) {}
