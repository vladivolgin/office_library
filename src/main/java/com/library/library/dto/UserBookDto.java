package com.library.library.dto;

public record UserBookDto(
        Long id,
        String title,
        String genre,
        Integer publishYear
) {}
