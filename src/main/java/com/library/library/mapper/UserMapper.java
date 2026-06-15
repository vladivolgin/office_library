package com.library.library.mapper;

import com.library.library.dto.UserBookDto;
import com.library.library.dto.UserDto;
import com.library.library.dao.entity.Book;
import com.library.library.dao.entity.User;

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
                takenBook,
                u.isEnabled()
        );
    }

}
