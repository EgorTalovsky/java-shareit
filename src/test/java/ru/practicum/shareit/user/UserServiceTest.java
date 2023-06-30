package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.IncorrectEmailException;
import ru.practicum.shareit.user.impl.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void createUserWithoutEmail_whenCreate_thenThrowsException() {
        User user = new User();

        assertThrows(IncorrectEmailException.class, () -> userService.createUser(user));

    }

    @Test
    void createUserWithNotCorrectEmail_whenCreate_thenThrowsException() {
        User user = new User();
        user.setEmail("user.ya.ru");

        assertThrows(IncorrectEmailException.class, () -> userService.createUser(user));
    }

    @Test
    void createUserWithEmail_whenCreate_thenSaved() {
        User user = new User();
        user.setEmail("user@ya.ru");

        when(userService.createUser(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertEquals(createdUser, user);
    }

    @Test
    void getAllUsersTest() {
        List<User> users = new ArrayList<>();
        Collection<User> usersBeforeMethodCall = userService.getAllUsers();

        assertEquals(users, usersBeforeMethodCall);
    }
}