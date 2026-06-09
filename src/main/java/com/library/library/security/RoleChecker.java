package com.library.library.security;

import com.library.library.entity.User;
import com.library.library.entity.UserRole;
import com.library.library.exception.ForbiddenException;
import com.library.library.exception.NotFoundException;
import com.library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleChecker {

    private final UserRepository userRepository;

    // Получить пользователя по ID из заголовка
    public User getUser(Long userId) {
        if (userId == null) {
            throw new ForbiddenException("Заголовок X-User-Id обязателен");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
    }

    // Проверить что пользователь — EDITOR
    public void requireEditor(Long userId) {
        User user = getUser(userId);
        if (user.getRole() != UserRole.EDITOR) {
            throw new ForbiddenException("Доступ запрещён: требуется роль EDITOR");
        }
    }
}
