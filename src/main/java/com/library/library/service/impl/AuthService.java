package com.library.library.service.impl;

import com.library.library.dto.RegisterDto;
import com.library.library.dto.UserDto;
import com.library.library.dao.entity.User;
import com.library.library.exception.ConflictException;
import com.library.library.mapper.UserMapper;
import com.library.library.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(RegisterDto dto) {
        // Проверяем уникальность логина заранее, чтобы вернуть понятный 409 Conflict,
        // а не упасть с 500 на UNIQUE-constraint в БД.
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new ConflictException("Логин уже занят: " + dto.username());
        }

        User user = new User();
        user.setUsername(dto.username());
        // PasswordEncoder = BCrypt: хеш с солью, пароль в БД никогда не хранится в открытом виде.
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setFullName(dto.fullName());
        user.setBirthYear(dto.birthYear());
        user.setRole(dto.role());

        // Возвращаем DTO, а не Entity, чтобы хеш пароля не утёк наружу через API.
        return UserMapper.toDto(userRepository.save(user));
    }
}
