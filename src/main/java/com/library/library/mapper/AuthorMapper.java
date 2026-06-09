package com.library.library.mapper;

import com.library.library.dto.AuthorDto;
import com.library.library.entity.Author;

public class AuthorMapper {
    private AuthorMapper() {}

    public static AuthorDto toDto(Author a) {
        return new AuthorDto(a.getId(), a.getFullName(), a.getBirthYear(), a.getBiography());
    }

    public static Author toEntity(AuthorDto dto) {
        Author a = new Author();
        a.setFullName(dto.fullName());
        a.setBirthYear(dto.birthYear());
        a.setBiography(dto.biography());
        return a;
    }
}
