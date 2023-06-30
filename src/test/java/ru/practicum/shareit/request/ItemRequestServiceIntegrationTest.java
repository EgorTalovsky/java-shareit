package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "shareit=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    UserService userService;

    @DirtiesContext
    @Test
    void addRequest() {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@ya.ru");
        userService.createUser(requestor);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("i want");
        itemRequest.setRequestor(requestor);

        assertEquals(itemRequestService.addRequest(itemRequest).getId(), 1L);
        assertEquals(itemRequestService.addRequest(itemRequest).getDescription(), "i want");
    }

    @DirtiesContext
    @Test
    void getAllRequestsByRequestorId() {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@ya.ru");
        userService.createUser(requestor);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("i want");
        itemRequest.setRequestor(requestor);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("i want2");
        itemRequest2.setRequestor(requestor);

        itemRequestService.addRequest(itemRequest);
        itemRequestService.addRequest(itemRequest2);

        assertEquals(2,
                itemRequestService.getAllRequestsByRequestorId(1L).size());
    }

    @DirtiesContext
    @Test
    void getAllRequests() {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@ya.ru");
        userService.createUser(requestor);

        User searcher = new User();
        searcher.setName("requestor2");
        searcher.setEmail("requestor2@ya.ru");
        userService.createUser(searcher);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("i want");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("i want2");
        itemRequest2.setRequestor(requestor);
        itemRequest2.setCreated(LocalDateTime.now());

        itemRequestService.addRequest(itemRequest);
        itemRequestService.addRequest(itemRequest2);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(2,
                itemRequestService.getAllRequests(2L, pageable).size());
    }

    @DirtiesContext
    @Test
    void getItemRequestById() {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@ya.ru");
        userService.createUser(requestor);

        User searcher = new User();
        searcher.setName("requestor2");
        searcher.setEmail("requestor2@ya.ru");
        userService.createUser(searcher);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("i want");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("i want2");
        itemRequest2.setRequestor(requestor);
        itemRequest2.setCreated(LocalDateTime.now());

        itemRequestService.addRequest(itemRequest);
        itemRequestService.addRequest(itemRequest2);

        assertEquals(itemRequestService.getItemRequestById(1L).getDescription(), "i want");
        assertEquals(itemRequestService.getItemRequestById(2L).getDescription(), "i want2");
    }

    @Test
    void getItemRequestById_whenRequestNotFound_thenThrowsException() {
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1));
    }

    @DirtiesContext
    @Test
    void getFullItemRequestById() {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@ya.ru");
        userService.createUser(requestor);

        User requestor2 = new User();
        requestor2.setName("requestor2");
        requestor2.setEmail("requestor2@ya.ru");
        userService.createUser(requestor2);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("i want");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("i want2");
        itemRequest2.setRequestor(requestor2);
        itemRequest2.setCreated(LocalDateTime.now());

        itemRequestService.addRequest(itemRequest);
        itemRequestService.addRequest(itemRequest2);

        List<Item> items = new ArrayList<>();

        assertEquals(itemRequestService.getFullItemRequestById(1L).getRequestor(), requestor);
        assertEquals(itemRequestService.getFullItemRequestById(2L).getRequestor(), requestor2);
    }

    @Test
    void getFullItemRequestById_whenRequestNotFound_thenThrowsException() {
        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getFullItemRequestById(1));
    }
}