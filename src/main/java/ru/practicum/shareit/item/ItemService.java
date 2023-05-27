package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(long id, ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    ItemDto getItemById(long id);

    List<ItemDto> getItemsBySharer(long id);

    List<Item> searchItem(String text);
}
