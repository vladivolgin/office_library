package com.library.library.dto;

import com.library.library.entity.UserRole;

public record UserDto(
        Long id,
        String fullName,
        Integer birthYear,
        UserRole role
) {}
