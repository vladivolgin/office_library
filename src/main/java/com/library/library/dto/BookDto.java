package com.library.library.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record BookDto(
        Long id,
        String title,
        Integer publishYear,
        String genre,
        Set<AuthorDto> authors,
        Long takenByUserId,
        LocalDateTime takenAt
) {}
