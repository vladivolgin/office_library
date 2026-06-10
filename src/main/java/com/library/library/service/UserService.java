package com.library.library.service;

import com.library.library.dto.UserDto;
import com.library.library.entity.User;
import com.library.library.exception.NotFoundException;
import com.library.library.mapper.UserMapper;
import com.library.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User user = findById(id);
        user.setFullName(dto.fullName());
        user.setBirthYear(dto.birthYear());
        user.setRole(dto.role());
        return UserMapper.toDto(userRepository.save(user));
    }

    // Удалить пользователя
    @Transactional
    public void delete(Long id) {
        findById(id);
        userRepository.deleteById(id);
    }
}
