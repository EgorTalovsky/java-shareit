package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User createUser(User user);

    Collection<User> getAllUsers();

    User getUserById(long userId);

    User updateUser(User user, long userId);

    void deleteUserById(long userId);
}
