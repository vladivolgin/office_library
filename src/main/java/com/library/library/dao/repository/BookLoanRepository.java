package com.library.library.dao.repository;

import com.library.library.dao.entity.BookLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

    // Текущая (не возвращённая) выдача книги, если есть
    Optional<BookLoan> findFirstByBookIdAndReturnedAtIsNull(Long bookId);

    // История выдач конкретной книги, сначала самые новые
    List<BookLoan> findByBookIdOrderByTakenAtDesc(Long bookId);

    // Количество выдач (рейтинг популярности) по каждой книге
    @Query("select bl.book.id, count(bl) from BookLoan bl group by bl.book.id")
    List<Object[]> countLoansGroupedByBookId();

    boolean existsByUserId(Long userId);
}
