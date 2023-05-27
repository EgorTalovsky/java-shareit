package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectFieldByItemException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserController userController;

    public Item addItem(long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
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

    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        return ItemMapper.toItemDto(itemRepository.updateItem(itemDto, id, userId));
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
