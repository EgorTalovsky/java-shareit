package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(Item item);

    Item updateItem(Item item, long id, long userId);

    Item getItemById(long id);

    List<ItemDto> getItemsBySharer(long id);

    List<Item> getAllItems();

    List<Item> searchItem(String text);
}
