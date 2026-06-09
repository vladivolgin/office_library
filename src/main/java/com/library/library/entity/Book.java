package com.library.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Column(name = "genre")
    private String genre;

    // Связь Many-to-Many с авторами
    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    // Пользователь который взял книгу (null = книга свободна)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taken_by_user_id")
    private User takenByUser;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Удобный метод: свободна ли книга?
    public boolean isAvailable() {
        return takenByUser == null;
    }
}
