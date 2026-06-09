package com.library.library.controller;

import com.library.library.dto.UserDto;
import com.library.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users
    @GetMapping
    public List<UserDto> getAll() {
        return userService.findAll();
    }

    // GET /api/users/1
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.findByIdDto(id);
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    // PUT /api/users/1
    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto) {
        return userService.update(id, dto);
    }

    // DELETE /api/users/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
