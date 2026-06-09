package com.library.library.mapper;

import com.library.library.dto.UserBookDto;
import com.library.library.dto.UserDto;
import com.library.library.entity.Book;
import com.library.library.entity.User;

public class UserMapper {
    private UserMapper() {}

    public static UserDto toDto(User u) {
        UserBookDto takenBook = null;
        if (u.getTakenBook() != null) {
            Book b = u.getTakenBook();
            takenBook = new UserBookDto(b.getId(), b.getTitle(), b.getGenre(), b.getPublishYear());
        }
        return new UserDto(
                u.getId(),
                u.getFullName(),
                u.getBirthYear(),
                u.getRole(),
                takenBook
        );
    }

    public static User toEntity(UserDto dto) {
        User u = new User();
        u.setFullName(dto.fullName());
        u.setBirthYear(dto.birthYear());
        u.setRole(dto.role());
        return u;
    }
}
