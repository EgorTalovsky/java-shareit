package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userServiceImpl;

    @PostMapping
    public User createUser(@RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userServiceImpl.createUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        return userServiceImpl.getUserById(id);
    }

    @PatchMapping("/{id}")
    public User updateUser(@RequestBody UserDto userDto,
                           @PathVariable long id) {
        User user = UserMapper.toUser(userDto);
        return userServiceImpl.updateUser(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        userServiceImpl.deleteUserById(id);
    }
}
