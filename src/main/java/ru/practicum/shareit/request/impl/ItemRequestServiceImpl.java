package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    @Autowired
    private final ItemRequestRepository itemRequestRepository;
    @Autowired
    private final UserService userService;

    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long requestorId) {
        User requestor = userService.getUserById(requestorId);
        ItemRequest itemRequest = new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                LocalDateTime.now());
        if (itemRequest.getItems() != null) {
            return ItemRequestMapper.toItemRequestDtoWithItems(itemRequestRepository.save(itemRequest));
        } else {
            return ItemRequestMapper.toItemRequestDtoWithoutItems(itemRequestRepository.save(itemRequest));
        }

    }

    public List<ItemRequestDto> getAllRequestsByRequestorId(long requestorId) {
        userService.getUserById(requestorId);
        return itemRequestRepository.findAllRequestsByRequestorId(requestorId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoWithItems)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(long requestorId, Pageable pageable) {
        return itemRequestRepository.findAllRequestsByRequestorIdNot(requestorId, pageable)
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoWithItems)
                .sorted(((o1, o2) -> o2.getCreated().compareTo(o1.getCreated())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto getItemRequestById(long requestId, long userId) {
        userService.getUserById(userId);
        return ItemRequestMapper.toItemRequestDtoWithItems(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("запрос не найден")));
    }

    public ItemRequest getFullItemRequestById(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("запрос не найден"));
    }
}
