package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(long id, Item item);

    Item updateItem(Item item, long id, long userId);

    Item getItemById(long itemId);

    List<ItemWithBookingsDto> getItemsByOwner(long id);

    List<Item> searchItem(String text);
}
