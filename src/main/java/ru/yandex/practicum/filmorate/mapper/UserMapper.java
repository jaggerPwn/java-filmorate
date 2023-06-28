package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    private UserMapper() {
    }

    public static UserDTO userToDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

    public static User dtoToUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new IllegalArgumentException("userDTO cannot be null");
        }

        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .login(userDTO.getLogin())
                .name(userDTO.getName())
                .birthday(userDTO.getBirthday())
                .build();
    }

    public static List<UserDTO> listUsersToListDto(Collection<User> users) {
        return users.stream().map(UserMapper::userToDTO).collect(Collectors.toList());
    }

}
