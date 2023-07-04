package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    @Autowired
    private final ItemService itemService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final ItemRequestServiceImpl itemRequestService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @RequestBody @Valid ItemDto itemDto) {
        Item item = new Item();
        long requestId = itemDto.getRequestId();
        if (requestId > 0) {
            ItemRequest request = itemRequestService.getFullItemRequestById(requestId);
            item = ItemMapper.toItem(itemDto);
            request.getItems().add(item);
            item.setRequest(request);
            itemDto.setRequestId(requestId);
            return ItemMapper.toItemDtoWithRequest(itemService.addItem(userId, item));
        } else {
            item = ItemMapper.toItem(itemDto);
            return ItemMapper.toItemDto(itemService.addItem(userId, item));
        }
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return itemService.updateItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsAndCommentsDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable long itemId) {
        ItemDto itemDto = itemService.getItemById(itemId);
        long ownerId = itemDto.getOwner().getId();
        if (ownerId == userId) {
            return itemService.getItemsByOwner(userId)
                    .stream()
                    .filter(i -> i.getId() == itemId)
                    .collect(Collectors.toList())
                    .get(0);
        }
        return new ItemWithBookingsAndCommentsDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null,
                itemDto.getComments());
    }

    @GetMapping
    public List<ItemWithBookingsAndCommentsDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Valid CommentDto commentDto,
                                 @PathVariable long itemId) {
        Comment comment = new Comment(
                commentDto.getId(),
                commentDto.getText(),
                ItemMapper.toItem(itemService.getItemById(itemId)),
                userService.getUserById(userId),
                LocalDateTime.now());
        return itemService.addComment(comment);
    }
}
