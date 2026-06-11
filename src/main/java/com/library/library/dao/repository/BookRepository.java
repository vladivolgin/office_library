package com.library.library.dao.repository;

import com.library.library.dao.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Поиск книг по фрагменту названия (case-insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);
}
