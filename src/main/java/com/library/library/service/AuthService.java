package com.library.library.service;

import com.library.library.dto.RegisterDto;
import com.library.library.dto.UserDto;
import com.library.library.entity.User;
import com.library.library.exception.ConflictException;
import com.library.library.mapper.UserMapper;
import com.library.library.repository.UserRepository;
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
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new ConflictException("Логин уже занят: " + dto.username());
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setFullName(dto.fullName());
        user.setBirthYear(dto.birthYear());
        user.setRole(dto.role());

        return UserMapper.toDto(userRepository.save(user));
    }
}
