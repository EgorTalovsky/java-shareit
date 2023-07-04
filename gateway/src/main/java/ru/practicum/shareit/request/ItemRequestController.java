package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.validate.PageValidator;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestBody @Valid ItemRequestDto itemRequestDto,
                                             @RequestHeader("X-Sharer-User-Id") long requestorId) {
        return itemRequestClient.addRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByRequestorId(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        return itemRequestClient.getAllRequestsByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        PageValidator.checkPageable(from, size);
        return itemRequestClient.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}
