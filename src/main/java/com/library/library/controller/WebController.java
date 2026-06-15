package com.library.library.controller;

import com.library.library.dto.AuthorBookDto;
import com.library.library.dto.AuthorDto;
import com.library.library.dto.BookDto;
import com.library.library.dto.RegisterDto;
import com.library.library.common.UserRole;
import com.library.library.dao.entity.User;
import com.library.library.exception.ConflictException;
import com.library.library.exception.ForbiddenException;
import com.library.library.exception.NotFoundException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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
        model.addAttribute("topBooks", bookService.findAll().stream().limit(5).toList());
        return "dashboard";
    }

    @GetMapping("/books")
    public String books(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentUserId", currentUser.getId());
        return "books";
    }

    @PostMapping("/books/{id}/take")
    public String takeBook(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            bookService.takeBook(id, authentication);
        } catch (ConflictException e) {
            redirectAttributes.addFlashAttribute("conflictError", e.getMessage());
        }
        return "redirect:/web/books";
    }

    @PostMapping("/books/{id}/return")
    public String returnBook(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            bookService.returnBook(id, authentication);
        } catch (ConflictException | ForbiddenException e) {
            redirectAttributes.addFlashAttribute("conflictError", e.getMessage());
        }
        return "redirect:/web/books";
    }

    @PostMapping("/books/{id}/availability")
    public String setBookAvailability(@PathVariable Long id,
                                       @RequestParam(value = "userId", required = false) Long userId,
                                       RedirectAttributes redirectAttributes) {
        try {
            bookService.setAvailability(id, userId);
        } catch (ConflictException | NotFoundException e) {
            redirectAttributes.addFlashAttribute("conflictError", e.getMessage());
        }
        return "redirect:/web/books";
    }

    @GetMapping("/books/{id}/history")
    public String bookHistory(@PathVariable Long id, Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("book", bookService.findByIdDto(id));
        model.addAttribute("loans", bookService.findLoanHistory(id));
        return "book-history";
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
    public String updateUserRole(@PathVariable Long id, @RequestParam UserRole role,
                                  RedirectAttributes redirectAttributes) {
        try {
            userService.updateRole(id, role);
        } catch (ConflictException e) {
            // Например, попытка понизить последнего EDITOR — показываем сообщение на /web/users
            redirectAttributes.addFlashAttribute("conflictError", e.getMessage());
        }
        return "redirect:/web/users";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("authors", authorRepository.findAll());
        if (!model.containsAttribute("bookDto")) {
            model.addAttribute("bookDto", new BookDto(null, null, null, null, null, null, null, 0L));
        }
        return "book-form";
    }

    @PostMapping("/books/new")
    public String createBook(@Valid @ModelAttribute("bookDto") BookDto dto, BindingResult bindingResult,
                              @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                              Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authors", authorRepository.findAll());
            return "book-form";
        }
        BookDto created;
        try {
            created = bookService.create(dto);
        } catch (ConflictException e) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authors", authorRepository.findAll());
            model.addAttribute("conflictError", e.getMessage());
            return "book-form";
        }
        if (authorIds != null) {
            for (Long authorId : authorIds) {
                bookService.addAuthor(created.id(), authorId);
            }
        }
        return "redirect:/web/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("authors", authorRepository.findAll());
        if (!model.containsAttribute("bookDto")) {
            model.addAttribute("bookDto", bookService.findByIdDto(id));
        }
        model.addAttribute("editMode", true);
        model.addAttribute("bookId", id);
        return "book-form";
    }

    @PostMapping("/books/{id}/edit")
    public String updateBook(@PathVariable Long id, @Valid @ModelAttribute("bookDto") BookDto dto, BindingResult bindingResult,
                              @RequestParam(value = "authorIds", required = false) List<Long> authorIds,
                              Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authors", authorRepository.findAll());
            model.addAttribute("editMode", true);
            model.addAttribute("bookId", id);
            return "book-form";
        }
        bookService.update(id, dto);
        bookService.updateAuthors(id, authorIds);
        return "redirect:/web/books";
    }

    @GetMapping("/authors/new")
    public String newAuthorForm(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        if (!model.containsAttribute("authorDto")) {
            List<AuthorBookDto> emptyBooks = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                emptyBooks.add(new AuthorBookDto(null, null, null));
            }
            model.addAttribute("authorDto", new AuthorDto(null, null, null, null, emptyBooks));
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

    @GetMapping("/authors/{id}/edit")
    public String editAuthorForm(@PathVariable Long id, Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        if (!model.containsAttribute("authorDto")) {
            AuthorDto author = authorService.findByIdDto(id);
            model.addAttribute("authorDto", new AuthorDto(author.id(), author.fullName(), author.birthYear(), author.biography(), List.of()));
        }
        model.addAttribute("editMode", true);
        model.addAttribute("authorId", id);
        return "author-form";
    }

    @PostMapping("/authors/{id}/edit")
    public String updateAuthor(@PathVariable Long id, @Valid @ModelAttribute("authorDto") AuthorDto dto, BindingResult bindingResult,
                                Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("editMode", true);
            model.addAttribute("authorId", id);
            return "author-form";
        }
        authorService.update(id, dto);
        return "redirect:/web/authors";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/web/books";
    }

    @PostMapping("/authors/{id}/delete")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return "redirect:/web/authors";
    }

    @PostMapping("/users/{id}/enabled")
    public String setUserEnabled(@PathVariable Long id, @RequestParam boolean enabled,
                                  Authentication authentication, RedirectAttributes redirectAttributes) {
        User currentUser = (User) authentication.getPrincipal();
        try {
            userService.setEnabled(id, enabled, currentUser.getId());
        } catch (ConflictException e) {
            redirectAttributes.addFlashAttribute("conflictError", e.getMessage());
        }
        return "redirect:/web/users";
    }
}
