package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.impl.ItemRequestServiceImpl;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void addRequest() {

    }

    @Test
    void getAllRequestsByRequestorId() {
    }

    @Test
    void getAllRequests() {
    }

    @Test
    void getItemRequestById() {

    }

    @Test
    void getFullItemRequestById() {
    }
}