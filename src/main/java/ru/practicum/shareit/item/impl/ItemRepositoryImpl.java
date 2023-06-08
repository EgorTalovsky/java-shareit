package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ItemRepositoryImpl {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;
    private final UserController userController;

    public ItemRepositoryImpl(UserController userController) {
        this.userController = userController;
    }

    public Item addItem(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item, long id, long userId) {
        userController.getUserById(userId);
        Item itemForUpdate = getItemById(id);
        if (itemForUpdate.getOwner().getId() != userId) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        return itemForUpdate;
    }

    public Item getItemById(long id) {
        return items.get(id);
    }

    public List<ItemDto> getItemsBySharer(long id) {
        userController.getUserById(id);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : getAllItems()) {
            if (item.getOwner().getId() == id) {
                itemsDto.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsDto;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public List<Item> searchItem(String text) {
        List<Item> foundItems = new ArrayList<>();
        if (!text.isEmpty()) {
            String requestToLowerCase = text.toLowerCase();
            for (Item item : getAllItems()) {
                if (item.getAvailable() &&
                        (item.getName().toLowerCase().contains(requestToLowerCase)
                                || item.getDescription().toLowerCase().contains(requestToLowerCase))) {
                    foundItems.add(item);
                }
            }
        }
        return foundItems;
    }
}
