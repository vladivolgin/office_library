package com.library.library.dto;

public record AuthorDto(
        Long id,
        String fullName,
        Integer birthYear,
        String biography
) {}
