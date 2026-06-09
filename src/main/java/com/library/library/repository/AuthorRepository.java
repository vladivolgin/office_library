package com.library.library.repository;

import com.library.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Поиск авторов по фрагменту ФИО (case-insensitive)
    List<Author> findByFullNameContainingIgnoreCase(String fullName);
}
