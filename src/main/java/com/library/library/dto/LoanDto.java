package com.library.library.dto;

import java.time.LocalDateTime;

public record LoanDto(
        Long id,
        String userFullName,
        LocalDateTime takenAt,
        LocalDateTime returnedAt
) {}
