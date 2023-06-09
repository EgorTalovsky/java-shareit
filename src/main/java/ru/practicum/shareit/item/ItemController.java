package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.impl.UserServiceImpl;

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
    private final ItemService itemService;
    private final UserServiceImpl userService;

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                        @RequestBody @Valid ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return itemService.updateItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
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
        return new ItemWithBookingsDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null,
                itemDto.getComments());
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
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
        commentDto.setCreated(LocalDateTime.now());
        commentDto.setItem(ItemMapper.toItem(itemService.getItemById(itemId)) );
        commentDto.setAuthorName(userService.getUserById(userId).getName());
        Comment comment = new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getItem(),
                userService.getUserById(userId),
                commentDto.getCreated());
        return itemService.addComment(comment);
    }
}
