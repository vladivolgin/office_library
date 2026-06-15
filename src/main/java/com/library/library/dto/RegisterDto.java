package com.library.library.dto;

import com.library.library.common.UserRole;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Year;

public record RegisterDto(
        @NotBlank(message = "Логин не может быть пустым")
        @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Логин может содержать только латинские буквы, цифры и подчёркивание")
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 6, max = 100, message = "Пароль должен быть не короче 6 символов")
        String password,

        @NotBlank(message = "ФИО пользователя не может быть пустым")
        @Size(min = 2, max = 100, message = "ФИО должно быть от 2 до 100 символов")
        String fullName,

        @NotNull(message = "Год рождения обязателен")
        @Min(value = 1900, message = "Год рождения не может быть раньше 1900")
        Integer birthYear,

        // Поле есть в DTO, т.к. AuthService.register() общий для /api и /web,
        // но из формы регистрации (register.jsp) всегда приходит скрытое
        // значение READER — пользователь не может выдать себе роль EDITOR.
        @NotNull(message = "Роль обязательна")
        UserRole role
) {
    @AssertTrue(message = "Регистрация доступна только с 14 лет")
    public boolean isAgeValid() {
        return birthYear == null || birthYear <= Year.now().getValue() - 14;
    }
}
