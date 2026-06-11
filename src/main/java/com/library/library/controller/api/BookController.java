package com.library.library.controller.api;

import com.library.library.dto.BookDto;
import com.library.library.service.impl.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public BookDto getById(@PathVariable Long id) {
        return bookService.findByIdDto(id);
    }

    @GetMapping("/search")
    public List<BookDto> search(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }

    @PostMapping
    public ResponseEntity<BookDto> create(@Valid @RequestBody BookDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.create(dto));
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable Long id, @RequestBody BookDto dto) {
        return bookService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookId}/authors/{authorId}")
    public BookDto addAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        return bookService.addAuthor(bookId, authorId);
    }

    @PostMapping("/{bookId}/take")
    public BookDto takeBook(@PathVariable Long bookId, Authentication authentication) {
        return bookService.takeBook(bookId, authentication);
    }

    @PostMapping("/{bookId}/return")
    public BookDto returnBook(@PathVariable Long bookId, Authentication authentication) {
        return bookService.returnBook(bookId, authentication);
    }
}
