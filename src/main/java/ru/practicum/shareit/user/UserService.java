package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public Collection<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User getUserById(long userId) {
        return userRepository.getUserById(userId);
    }

    public User updateUser(UserDto userDto, long userId) throws IllegalAccessException {
        return userRepository.updateUser(userDto, userId);
    }

    public void deleteUserById(long userId) {
        userRepository.deleteUserById(userId);
    }
}
