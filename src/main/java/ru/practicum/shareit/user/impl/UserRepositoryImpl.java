package ru.practicum.shareit.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.IncorrectEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserRepositoryImpl {
    private Map<Long, User> users = new HashMap<>();
    private Map<Long, String> userEmails = new HashMap<>();
    private long nextId = 1;

    public User createUser(User user) {
        String email = user.getEmail();
        if (email == null || !email.contains("@")) {
            throw new IncorrectEmailException("Некорректный адрес электронной почты");
        }
        if (userEmails.containsValue(email)) {
            throw new EmailAlreadyExistException("Пользователь уже зарегистрирован");
        } else {
            user.setId(nextId++);
            users.put(user.getId(), user);
            userEmails.put(user.getId(), email);
        }
        return user;
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUserById(long id) {
        return users.get(id);
    }

    public User updateUser(User user, long userId) {
        if (users.get(userId).getEmail().equals(user.getEmail())) {
            return users.get(userId);
        }
        if (userEmails.containsValue(user.getEmail())) {
            throw new EmailAlreadyExistException("С этой почтой обновить не получится");
        }
        User userForUpdate = getUserById(userId);
        if (user.getEmail() != null) {
            userForUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userForUpdate.setName(user.getName());
        }
        users.put(userId, userForUpdate);
        userEmails.put(userId, userForUpdate.getEmail());
        return userForUpdate;
    }

    public void deleteUserById(long userId) {
        userEmails.remove(userId);
        users.remove(userId);
    }
}
