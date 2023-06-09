package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingSimplifiedDto;
import ru.practicum.shareit.exception.BookingStateNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserController userController;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public Item addItem(long userId, Item item) {
        checkItemOwner(userId, item);
        return itemRepository.save(item);
    }

    public Item updateItem(Item item, long itemId, long userId) {
        userController.getUserById(userId);
        Item updatedItem = checkAndSetFieldsForUpdate(item, itemId);
        return itemRepository.save(updatedItem);
    }

    public ItemDto getItemById(long itemId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                comments);
    }

    public List<ItemWithBookingsDto> getItemsByOwner(long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        List<ItemWithBookingsDto> itemWithBookingsDtoList = new ArrayList<>();
        List<Item> items = itemRepository.findItemByOwnerId(ownerId);
        if (bookingRepository.findAllByItemOwnerId(ownerId).isEmpty()) {
            for (Item item : items) {
                List<CommentDto> comments = commentRepository.findAllByItemId(item.getId())
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList());
                ItemWithBookingsDto itemDto = new ItemWithBookingsDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        null,
                        null,
                        comments);
                itemWithBookingsDtoList.add(itemDto);
            }
        } else {
            for (Item item : items) {
                long itemId = item.getId();
                List<Booking> allBookingsOfItem = bookingRepository.findAllBookingsByItemId(itemId);
                List<Booking> pastBookingsOfItem = new ArrayList<>();
                List<Booking> futureBookingsOfItem = new ArrayList<>();
                for (Booking booking : allBookingsOfItem) {
                    if (booking.getEnd().isBefore(now) || booking.getStart().isBefore(now)) {
                        pastBookingsOfItem.add(booking);
                    }
                    if (booking.getStart().isAfter(now)) {
                        futureBookingsOfItem.add(booking);
                    }
                }
                BookingSimplifiedDto lastBookingDto = null;
                if (!pastBookingsOfItem.isEmpty()) {
                    Booking lastBooking = pastBookingsOfItem
                            .stream()
                            .sorted(((o2, o1) -> o1.getStart().compareTo(o2.getStart())))
                            .reduce((e2, e1) -> e2)
                            .orElse(null);
                    lastBookingDto = new BookingSimplifiedDto(
                            lastBooking.getId(),
                            lastBooking.getBooker().getId());

                }
                BookingSimplifiedDto nextBookingDto = null;
                if (!futureBookingsOfItem.isEmpty()) {
                    Booking nextBooking = futureBookingsOfItem
                            .stream()
                            .filter(o1 -> o1.getStatus().equals(BookingStatus.APPROVED))
                            .sorted(((o1, o2) -> o2.getStart().compareTo(o1.getStart())))
                            .reduce((e1, e2) -> e2)
                            .orElse(null);
                    if (nextBooking != null) {
                        nextBookingDto = new BookingSimplifiedDto(
                                nextBooking.getId(),
                                nextBooking.getBooker().getId());
                    }
                }
                List<CommentDto> comments = commentRepository.findAllByItemId(item.getId())
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList());
                ItemWithBookingsDto itemDto = new ItemWithBookingsDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        lastBookingDto,
                        nextBookingDto,
                        comments);
                itemWithBookingsDtoList.add(itemDto);
            }
        }
        return itemWithBookingsDtoList
                .stream()
                .sorted(Comparator.comparingLong(ItemWithBookingsDto::getId))
                .collect(Collectors.toList());
    }

    public List<Item> searchItem(String text) {
        if (text.isEmpty())
            return new ArrayList<>();
        return itemRepository.search(text);
    }

    public CommentDto addComment(Comment comment) {
        List<Booking> bookings = bookingRepository.findAllBookingsByBookerId(comment.getAuthor().getId())
                .stream()
                .filter(o1 -> o1.getItem().getId() == comment.getItem().getId())
                .filter(o1 -> o1.getStatus().equals(BookingStatus.APPROVED))
                .filter(o1 -> o1.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new BookingStateNotFoundException("вы не арендовали эту вещь");
        }
        commentRepository.save(comment);
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    private void checkItemOwner(long userId, Item item) {
        User owner = userController.getUserById(userId);
        if (owner != null) {
            item.setOwner(owner);
        } else {
            throw new UserNotFoundException("Владелец не найден");
        }
    }

    private Item checkAndSetFieldsForUpdate(Item item, long itemId) {
        Item updatedItem = ItemMapper.toItem(getItemById(itemId));
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        return updatedItem;
    }

}
