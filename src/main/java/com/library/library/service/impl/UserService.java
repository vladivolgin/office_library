package com.library.library.service.impl;

import com.library.library.dto.UserDto;
import com.library.library.dao.entity.User;
import com.library.library.exception.ConflictException;
import com.library.library.exception.NotFoundException;
import com.library.library.mapper.UserMapper;
import com.library.library.common.UserRole;
import com.library.library.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Получить всех пользователей
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    // Получить пользователя по ID — внутренний метод
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
    }

    // Получить пользователя по ID — DTO
    public UserDto findByIdDto(Long id) {
        return UserMapper.toDto(findById(id));
    }

    // Обновить пользователя
    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User user = findById(id);
        checkLastEditorDemotion(user, dto.role());
        user.setFullName(dto.fullName());
        user.setBirthYear(dto.birthYear());
        user.setRole(dto.role());
        return UserMapper.toDto(userRepository.save(user));
    }

    // Изменить роль пользователя
    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public UserDto updateRole(Long id, UserRole role) {
        User user = findById(id);
        checkLastEditorDemotion(user, role);
        user.setRole(role);
        return UserMapper.toDto(userRepository.save(user));
    }

    // Запрещаем понижать роль последнего EDITOR — иначе никто больше
    // не сможет управлять ролями/книгами/авторами через веб-интерфейс.
    private void checkLastEditorDemotion(User user, UserRole newRole) {
        if (user.getRole() == UserRole.EDITOR
                && newRole != UserRole.EDITOR
                && userRepository.countByRole(UserRole.EDITOR) <= 1) {
            throw new ConflictException("Невозможно понизить роль: должен остаться хотя бы один EDITOR");
        }
    }

    // Блокировка/разблокировка пользователя
    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public UserDto setEnabled(Long id, boolean enabled, Long currentUserId) {
        User user = findById(id);
        if (!enabled) {
            if (id.equals(currentUserId)) {
                throw new ConflictException("Нельзя заблокировать самого себя");
            }
            if (user.getRole() == UserRole.EDITOR && userRepository.countByRole(UserRole.EDITOR) <= 1) {
                throw new ConflictException("Невозможно заблокировать: должен остаться хотя бы один EDITOR");
            }
        }
        user.setEnabled(enabled);
        return UserMapper.toDto(userRepository.save(user));
    }

    // Удалить пользователя
    @Transactional
    public void delete(Long id) {
        findById(id);
        userRepository.deleteById(id);
    }
}
