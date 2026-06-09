package com.library.library.controller;

import com.library.library.dto.BookDto;
import com.library.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<BookDto> create(
            @RequestBody BookDto dto,
            @RequestHeader("X-User-Id") Long requesterId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.create(dto, requesterId));
    }

    @PutMapping("/{id}")
    public BookDto update(
            @PathVariable Long id,
            @RequestBody BookDto dto,
            @RequestHeader("X-User-Id") Long requesterId) {
        return bookService.update(id, dto, requesterId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long requesterId) {
        bookService.delete(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookId}/authors/{authorId}")
    public BookDto addAuthor(
            @PathVariable Long bookId,
            @PathVariable Long authorId,
            @RequestHeader("X-User-Id") Long requesterId) {
        return bookService.addAuthor(bookId, authorId, requesterId);
    }

    @PostMapping("/{bookId}/take")
    public BookDto takeBook(@PathVariable Long bookId, @RequestParam Long userId) {
        return bookService.takeBook(bookId, userId);
    }

    @PostMapping("/{bookId}/return")
    public BookDto returnBook(@PathVariable Long bookId) {
        return bookService.returnBook(bookId);
    }
}
