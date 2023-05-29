package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User createUser(User user);

    Collection<User> getAllUsers();

    User getUserById(long id);

    User updateUser(UserDto userDto, long userId);

    void deleteUserById(long userId);
}
