package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(long id, Item item);

    Item updateItem(Item item, long id, long userId);

    ItemDto getItemById(long itemId);

    List<ItemWithBookingsAndCommentsDto> getItemsByOwner(long id);

    List<Item> searchItem(String text);

    CommentDto addComment(Comment comment);

    void setItemOwner(long userId, Item item);

    Item setFieldsForUpdate(Item item, long itemId);
}
