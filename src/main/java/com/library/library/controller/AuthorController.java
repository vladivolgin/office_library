package com.library.library.controller;

import com.library.library.dto.AuthorDto;
import com.library.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    // GET /api/authors — все авторы
    @GetMapping
    public List<AuthorDto> getAll() {
        return authorService.findAll();
    }
    // GET /api/authors/1 — автор по ID
    @GetMapping("/{id}")
    public AuthorDto getById(@PathVariable Long id) {
        return authorService.findByIdDto(id);
    }
    // GET /api/authors/search?name=пушк — поиск по имени
    @GetMapping("/search")
    public List<AuthorDto> search(@RequestParam String name) {
        return authorService.searchByName(name);
    }
    // POST /api/authors — создать автора
    @PostMapping
    public ResponseEntity<AuthorDto> create(
            @RequestBody AuthorDto dto,
            @RequestHeader("X-User-Id") Long requesterId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authorService.create(dto, requesterId));
    }
    // PUT /api/authors/1 — обновить автора
    @PutMapping("/{id}")
    public AuthorDto update(
            @PathVariable Long id,
            @RequestBody AuthorDto dto,
            @RequestHeader("X-User-Id") Long requesterId) {
        return authorService.update(id, dto, requesterId);
    }
    // DELETE /api/authors/1 — удалить автора
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId) {
        authorService.delete(id, requesterId);
        return ResponseEntity.noContent().build();
    }
}






















