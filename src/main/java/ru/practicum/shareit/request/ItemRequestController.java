package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest addRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                  @RequestHeader("X-Sharer-User-Id") long requestorId) {
        User requestor = userService.getUserById(requestorId);
        ItemRequest itemRequest = new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now());
        return itemRequestService.addRequest(itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        User requestor = userService.getUserById(requestorId);
        return itemRequestService.getAllRequestsByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        Pageable pageable = PageRequest.of(from, size);
        return itemRequestService.getAllRequests(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long requestId) {
        User user = userService.getUserById(userId);
        return itemRequestService.getItemRequestById(requestId);
    }
}
