package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryImpl userRepository;

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    public User updateUser(User user, long userId) {
        return userRepository.updateUser(user, userId);
    }

    public void deleteUserById(long userId) {
        userRepository.deleteUserById(userId);
    }
}
