package com.library.library.service;
import com.library.library.service.impl.UserService;

import com.library.library.dto.UserDto;
import com.library.library.dao.entity.User;
import com.library.library.common.UserRole;
import com.library.library.exception.ConflictException;
import com.library.library.exception.NotFoundException;
import com.library.library.dao.repository.BookLoanRepository;
import com.library.library.dao.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookLoanRepository bookLoanRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFullName("Иван Иванов");
        user.setBirthYear(1990);
        user.setRole(UserRole.READER);
    }

    @Test
    void findById_throwsNotFound_whenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_updatesFields() {
        UserDto dto = new UserDto(null, "Пётр Петров", 1985, UserRole.EDITOR, null, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.update(1L, dto);

        assertThat(result.fullName()).isEqualTo("Пётр Петров");
        assertThat(result.birthYear()).isEqualTo(1985);
        assertThat(result.role()).isEqualTo(UserRole.EDITOR);
    }

    @Test
    void updateRole_throwsConflict_whenDemotingLastEditor() {
        user.setRole(UserRole.EDITOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(UserRole.EDITOR)).thenReturn(1L);

        assertThatThrownBy(() -> userService.updateRole(1L, UserRole.READER))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateRole_allowsDemotion_whenAnotherEditorExists() {
        user.setRole(UserRole.EDITOR);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.countByRole(UserRole.EDITOR)).thenReturn(2L);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.updateRole(1L, UserRole.READER);

        assertThat(result.role()).isEqualTo(UserRole.READER);
    }

    @Test
    void delete_removesUser_whenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_throwsNotFound_whenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(1L))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).deleteById(any());
    }
}
