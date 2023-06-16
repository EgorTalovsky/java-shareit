package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.impl.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public User createUser(@RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userService.createUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}")
    public User updateUser(@RequestBody UserDto userDto,
                           @PathVariable long id) {
        User user = UserMapper.toUser(userDto);
        return userService.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);
    }
}
