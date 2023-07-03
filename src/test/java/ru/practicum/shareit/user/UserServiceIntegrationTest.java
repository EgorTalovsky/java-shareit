package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "shareit=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    @Autowired
    UserService userService;

    @DirtiesContext
    @Test
    void getUserById_whenUserFound_thenReturnUser() {
        User user = new User();
        user.setEmail("user@ya.ru");
        user.setName("name");
        userService.createUser(user);

        assertEquals(userService.getUserById(1), user);
    }

    @DirtiesContext
    @Test
    void getUserById_whenUserNotFound_thenThrowsException() {
        User user = new User();
        user.setEmail("user@ya.ru");
        user.setName("name");
        userService.createUser(user);

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(2));
    }

    @DirtiesContext
    @Test
    void updateUser_whenCorrectFields_thenUpdate() {
        User user = new User();
        user.setName("new_name");
        user.setEmail("new_user@ya.ru");
        User updatedUser = new User();
        updatedUser.setEmail("old_email@ya.ru");
        updatedUser.setName("old_name");

        userService.createUser(updatedUser);

        assertEquals("new_name", userService.updateUser(user, 1L).getName());
    }

    @DirtiesContext
    @Test
    void updateUser_whenNotCorrectFields_thenNotUpdate() {
        User user = new User();
        User updatedUser = new User();
        updatedUser.setEmail("old_email@ya.ru");
        updatedUser.setName("old_name");

        userService.createUser(updatedUser);

        assertEquals("old_name", userService.updateUser(user, 1L).getName());
    }

    @DirtiesContext
    @Test
    void deleteUserById_whenDelete_thenSizeOfCollectionIs0() {
        User user = new User();
        user.setName("name");
        user.setEmail("user@ya.ru");
        userService.createUser(user);

        assertEquals(1, userService.getAllUsers().size());

        userService.deleteUserById(1L);
        assertEquals(0, userService.getAllUsers().size());
    }
}
