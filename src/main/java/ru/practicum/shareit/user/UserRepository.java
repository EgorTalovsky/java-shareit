package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.IncorrectEmailException;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserRepository {
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

    public User updateUser(UserDto userDto, long userId) {
        if (users.get(userId).getEmail().equals(userDto.getEmail())) {
            return users.get(userId);
        }
        if (userEmails.containsValue(userDto.getEmail())) {
            throw new EmailAlreadyExistException("С этой почтой обновить не получится");
        }
        User userForUpdate = getUserById(userId);
        if (userDto.getEmail() != null) {
            userForUpdate.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userForUpdate.setName(userDto.getName());
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
