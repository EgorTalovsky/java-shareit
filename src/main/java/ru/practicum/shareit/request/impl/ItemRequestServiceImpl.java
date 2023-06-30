package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequest addRequest(ItemRequest itemRequest) {
        return itemRequestRepository.save(itemRequest);
    }

    public List<ItemRequestDto> getAllRequestsByRequestorId(long requestorId) {
        return itemRequestRepository.findAllRequestsByRequestorId(requestorId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(long requestorId, Pageable pageable) {
        return itemRequestRepository.findAllRequestsByOtherUsers(requestorId, pageable)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .sorted(((o1, o2) -> o2.getCreated().compareTo(o1.getCreated())))
                .collect(Collectors.toList());
    }

    public ItemRequestDto getItemRequestById(long requestId) {
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("запрос не найден")));
    }

    public ItemRequest getFullItemRequestById(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserNotFoundException("запрос не найден"));
    }
}
