package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectFieldByItemException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserController;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepositoryImpl itemRepository;
    private final UserController userController;

    public Item addItem(long userId, Item item) {
        User sharer = userController.getUserById(userId);
        if (sharer != null) {
            item.setOwner(sharer);
        } else {
            throw new UserNotFoundException("Владелец не найден");
        }
        if (item.getAvailable() == null) {
            throw new IncorrectFieldByItemException("Нет информации о доступности для аренды");
        }
        if (item.getName().isEmpty() || item.getDescription() == null) {
            throw new IncorrectFieldByItemException("Имя или описание пустое");
        }
        return itemRepository.addItem(item);
    }

    public ItemDto updateItem(Item item, long id, long userId) {
        return ItemMapper.toItemDto(itemRepository.updateItem(item, id, userId));
    }

    public ItemDto getItemById(long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    public List<ItemDto> getItemsBySharer(long id) {
        return itemRepository.getItemsBySharer(id);
    }

    public List<Item> searchItem(String text) {
        return itemRepository.searchItem(text);
    }
}
