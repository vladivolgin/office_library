package com.library.library.service;

import com.library.library.dto.AuthorDto;
import com.library.library.entity.Author;
import com.library.library.exception.NotFoundException;
import com.library.library.mapper.AuthorMapper;
import com.library.library.repository.AuthorRepository;
import com.library.library.security.RoleChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final RoleChecker roleChecker;

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

    @Transactional
    public AuthorDto create(AuthorDto dto, Long requesterId) {
        roleChecker.requireEditor(requesterId);
        Author author = AuthorMapper.toEntity(dto);
        return AuthorMapper.toDto(authorRepository.save(author));
    }

    @Transactional
    public AuthorDto update(Long id, AuthorDto dto, Long requesterId) {
        roleChecker.requireEditor(requesterId);
        Author author = findById(id);
        author.setFullName(dto.fullName());
        author.setBirthYear(dto.birthYear());
        author.setBiography(dto.biography());
        return AuthorMapper.toDto(authorRepository.save(author));
    }

    @Transactional
    public void delete(Long id, Long requesterId) {
        roleChecker.requireEditor(requesterId);
        findById(id);
        authorRepository.deleteById(id);
    }
}
