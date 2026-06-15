package com.library.library.dao.repository;

import com.library.library.dao.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Поиск книг по фрагменту названия (case-insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Поиск книги-дубликата: то же название и год издания (case-insensitive)
    Optional<Book> findFirstByTitleIgnoreCaseAndPublishYear(String title, Integer publishYear);
}
