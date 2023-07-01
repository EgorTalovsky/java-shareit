package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "shareit=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    UserService userService;
    ItemRequestDto itemRequest = new ItemRequestDto();
    User requestor = new User();
    Pageable pageable = PageRequest.of(0, 10);


    @BeforeEach
    void createData() {
        requestor.setName("requestor");
        requestor.setEmail("requestor@ya.ru");
        userService.createUser(requestor);
        itemRequest.setDescription("i want");
        itemRequestService.addRequest(itemRequest, 1L);
    }

    @DirtiesContext
    @Test
    void addRequest() {
        assertEquals(itemRequestService.addRequest(itemRequest, 1L).getDescription(), "i want");
    }

    @DirtiesContext
    @Test
    void getAllRequestsByRequestorId() {
        assertEquals(1,
                itemRequestService.getAllRequestsByRequestorId(1L).size());
    }

    @DirtiesContext
    @Test
    void getAllRequests() {
        assertEquals(1, itemRequestService.getAllRequests(2L, pageable).size());
    }

    @DirtiesContext
    @Test
    void getItemRequestById() {
        assertEquals(itemRequestService.getItemRequestById(1L, 1L).getDescription(), "i want");
    }

    @DirtiesContext
    @Test
    void getItemRequestById_whenRequestNotFound_thenThrowsException() {
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequestById(2L, 1L));
    }

    @DirtiesContext
    @Test
    void getFullItemRequestById() {
        assertEquals(itemRequestService.getFullItemRequestById(1L).getRequestor(), requestor);
    }

    @DirtiesContext
    @Test
    void getFullItemRequestById_whenRequestNotFound_thenThrowsException() {
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getFullItemRequestById(22L));
    }
}