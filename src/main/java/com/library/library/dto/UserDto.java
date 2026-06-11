package com.library.library.dto;

import com.library.library.common.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDto(
        Long id,

        @NotBlank(message = "ФИО пользователя не может быть пустым")
        @Size(min = 2, max = 100, message = "ФИО должно быть от 2 до 100 символов")
        String fullName,

        @NotNull(message = "Год рождения обязателен")
        Integer birthYear,

        @NotNull(message = "Роль обязательна")
        UserRole role,

        UserBookDto takenBook
) {}
