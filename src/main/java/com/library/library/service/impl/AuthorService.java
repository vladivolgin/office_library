package com.library.library.service.impl;

import com.library.library.dto.AuthorDto;
import com.library.library.dao.entity.Author;
import com.library.library.exception.NotFoundException;
import com.library.library.mapper.AuthorMapper;
import com.library.library.dao.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Автор не найден: " + id));
    }

    public AuthorDto findByIdDto(Long id) {
        return AuthorMapper.toDto(findById(id));
    }

    public List<AuthorDto> searchByName(String name) {
        return authorRepository.findByFullNameContainingIgnoreCase(name).stream()
                .map(AuthorMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public AuthorDto create(AuthorDto dto) {
        Author author = AuthorMapper.toEntity(dto);
        return AuthorMapper.toDto(authorRepository.save(author));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public AuthorDto update(Long id, AuthorDto dto) {
        Author author = findById(id);
        author.setFullName(dto.fullName());
        author.setBirthYear(dto.birthYear());
        author.setBiography(dto.biography());
        return AuthorMapper.toDto(authorRepository.save(author));
    }

    @PreAuthorize("hasRole('EDITOR')")
    @Transactional
    public void delete(Long id) {
        findById(id);
        authorRepository.deleteById(id);
    }
}
