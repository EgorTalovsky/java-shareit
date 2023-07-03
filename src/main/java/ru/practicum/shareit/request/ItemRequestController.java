package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") long requestorId) {
        return itemRequestService.addRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id") long requestorId) {
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
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
