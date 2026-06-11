package com.library.library.service;

import com.library.library.common.UserRole;
import com.library.library.dao.entity.User;
import com.library.library.dao.repository.UserRepository;
import com.library.library.dto.RegisterDto;
import com.library.library.dto.UserDto;
import com.library.library.exception.ConflictException;
import com.library.library.service.impl.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    private RegisterDto dto;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder);
        dto = new RegisterDto("newuser", "rawpassword", "Иван Иванов", 1990, UserRole.READER);
    }

    @Test
    void register_throwsConflict_whenUsernameTaken() {
        // Логин уже существует -> регистрация должна упасть с ConflictException
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(ConflictException.class);

        // и сохранение нового пользователя при этом не должно вызываться
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_savesUser_withEncodedPasswordAndReaderRole() {
        // логин свободен
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        // пароль должен пройти через PasswordEncoder, а не сохраниться как есть
        when(passwordEncoder.encode("rawpassword")).thenReturn("encoded-hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDto result = authService.register(dto);

        // проверяем, какого именно User передали в save(...)
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(saved.getPassword()).isEqualTo("encoded-hash");
        assertThat(saved.getFullName()).isEqualTo("Иван Иванов");
        assertThat(saved.getBirthYear()).isEqualTo(1990);
        assertThat(saved.getRole()).isEqualTo(UserRole.READER);

        // и что метод вернул корректный DTO с тем же набором полей
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.fullName()).isEqualTo("Иван Иванов");
        assertThat(result.role()).isEqualTo(UserRole.READER);
    }
}
