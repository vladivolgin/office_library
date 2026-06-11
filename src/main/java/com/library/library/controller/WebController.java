package com.library.library.controller;

import com.library.library.dto.AuthorDto;
import com.library.library.dto.BookDto;
import com.library.library.dto.RegisterDto;
import com.library.library.common.UserRole;
import com.library.library.exception.ConflictException;
import com.library.library.dao.repository.AuthorRepository;
import com.library.library.dao.repository.BookRepository;
import com.library.library.dao.repository.UserRepository;
import com.library.library.service.impl.AuthService;
import com.library.library.service.impl.AuthorService;
import com.library.library.service.impl.BookService;
import com.library.library.service.impl.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class WebController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final AuthService authService;
    private final AuthorService authorService;
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        // Роль сразу проставляем READER — на форме это скрытое поле,
        // пользователь не может выбрать себе EDITOR.
        if (!model.containsAttribute("registerDto")) {
            model.addAttribute("registerDto", new RegisterDto(null, null, null, null, UserRole.READER));
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDto") RegisterDto dto, BindingResult bindingResult, Model model) {
        // Переиспользуем AuthService.register() и его валидацию — единая точка
        // регистрации для /api и /web, без дублирования логики.
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            authService.register(dto);
        } catch (ConflictException e) {
            // Логин занят — конфликт показываем прямо на форме, а не как 409 JSON,
            // т.к. это веб-страница, а не API-ответ.
            model.addAttribute("conflictError", e.getMessage());
            return "register";
        }

        return "redirect:/web/login?registered";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("bookCount", bookRepository.count());
        model.addAttribute("authorCount", authorRepository.count());
        model.addAttribute("userCount", userRepository.count());
        return "dashboard";
    }

    @GetMapping("/books")
    public String books(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("books", bookService.findAll());
        return "books";
    }

    @GetMapping("/authors")
    public String authors(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("authors", authorRepository.findAll());
        return "authors";
    }

    @GetMapping("/users")
    public String users(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", UserRole.values());
        return "users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam UserRole role) {
        userService.updateRole(id, role);
        return "redirect:/web/users";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        if (!model.containsAttribute("bookDto")) {
            model.addAttribute("bookDto", new BookDto(null, null, null, null, null, null, null));
        }
        return "book-form";
    }

    @PostMapping("/books/new")
    public String createBook(@Valid @ModelAttribute("bookDto") BookDto dto, BindingResult bindingResult,
                              Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            return "book-form";
        }
        bookService.create(dto);
        return "redirect:/web/books";
    }

    @GetMapping("/authors/new")
    public String newAuthorForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        if (!model.containsAttribute("authorDto")) {
            model.addAttribute("authorDto", new AuthorDto(null, null, null, null));
        }
        return "author-form";
    }

    @PostMapping("/authors/new")
    public String createAuthor(@Valid @ModelAttribute("authorDto") AuthorDto dto, BindingResult bindingResult,
                                Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            return "author-form";
        }
        authorService.create(dto);
        return "redirect:/web/authors";
    }
}
