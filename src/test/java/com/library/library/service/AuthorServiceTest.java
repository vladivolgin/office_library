package com.library.library.service;
import com.library.library.service.impl.AuthorService;

import com.library.library.dto.AuthorDto;
import com.library.library.dao.entity.Author;
import com.library.library.exception.NotFoundException;
import com.library.library.dao.repository.AuthorRepository;
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
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setFullName("Лев Толстой");
        author.setBirthYear(1828);
        author.setBiography("Русский писатель");
    }

    @Test
    void findById_throwsNotFound_whenAuthorMissing() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_savesAuthor() {
        AuthorDto dto = new AuthorDto(null, "Лев Толстой", 1828, "Русский писатель");
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorDto result = authorService.create(dto);

        assertThat(result.fullName()).isEqualTo("Лев Толстой");
        assertThat(result.birthYear()).isEqualTo(1828);
    }

    @Test
    void update_updatesFields() {
        AuthorDto dto = new AuthorDto(null, "Антон Чехов", 1860, "Драматург");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthorDto result = authorService.update(1L, dto);

        assertThat(result.fullName()).isEqualTo("Антон Чехов");
        assertThat(result.birthYear()).isEqualTo(1860);
        assertThat(result.biography()).isEqualTo("Драматург");
    }

    @Test
    void delete_removesAuthor_whenAuthorExists() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        authorService.delete(1L);

        verify(authorRepository).deleteById(1L);
    }

    @Test
    void delete_throwsNotFound_whenAuthorMissing() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.delete(1L))
                .isInstanceOf(NotFoundException.class);

        verify(authorRepository, never()).deleteById(any());
    }
}
