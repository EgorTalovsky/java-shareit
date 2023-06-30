package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addRequest(ItemRequest itemRequest);

    List<ItemRequestDto> getAllRequestsByRequestorId(long requestorId);

    List<ItemRequestDto> getAllRequests(long requestorId, Pageable pageable);

    ItemRequestDto getItemRequestById(long requestId);

    ItemRequest getFullItemRequestById(long requestId);
}
