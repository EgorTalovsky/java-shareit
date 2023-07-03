package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    private final User user1 = new User(1, "user1", "user1@ya.ru");
    private final User user2 = new User(2, "user2", "user2@ya.ru");
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    public void createNewUserTest() {
        when(userService.createUser(any())).thenReturn(user1);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).createUser(user1);
    }

    @SneakyThrows
    @Test
    void getAllUsersTest() {
        when(userService.getAllUsers()).thenReturn(List.of(new User()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUserById() {
        long userId = user1.getId();
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        long userId = user1.getId();
        User userToCreate = new User();
        userToCreate.setId(userId);
        User userUpdated = User.builder()
                .email(user1.getEmail())
                .id(userId)
                .build();
        when(userService.updateUser(userToCreate, userId)).thenReturn(userUpdated);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(userToCreate, userId);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long userId = user1.getId();
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(userId);
    }
}
