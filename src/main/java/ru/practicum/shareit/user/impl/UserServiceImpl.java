package ru.practicum.shareit.user.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectEmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService  {
    private final UserRepository userRepository;

    public User createUser(User user) {
        String email = user.getEmail();
        if (email == null || !email.contains("@")) {
            throw new IncorrectEmailException("Некорректный адрес электронной почты");
        }
        return userRepository.save(user);
    }

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такого пользователя у нас нет"));
    }

    public User updateUser(User user, long userId) {
        User updatedUser = getUserById(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        return userRepository.save(updatedUser);
    }

    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }
}
