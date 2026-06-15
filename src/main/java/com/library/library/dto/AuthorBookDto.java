package com.library.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

// Строка "Произведение" в форме добавления автора.
// Пустые строки (без названия) при сохранении пропускаются.
public record AuthorBookDto(
        @Size(max = 200, message = "Название произведения не может быть длиннее 200 символов")
        String title,

        @Min(value = 1000, message = "Год публикации не может быть раньше 1000")
        Integer publishYear,

        @Size(max = 50, message = "Жанр не может быть длиннее 50 символов")
        String genre
) {}
