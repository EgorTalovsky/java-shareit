package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "shareit=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserController userController;
    @Autowired
    BookingService bookingService;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    BookingController bookingController;
    @Autowired
    CommentRepository commentRepository;

    @DirtiesContext
    @Test
    void updateItem() {
        Item item = new Item();
        item.setName("new_name");
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@ya.ru");
        Item updatedItem = new Item();
        updatedItem.setName("old_name");
        updatedItem.setDescription("descrip");
        updatedItem.setAvailable(true);

        long userId = 1L;

        userController.createUser(user);
        itemService.addItem(userId, updatedItem);

        assertEquals("new_name", itemService.updateItem(item, userId, 1L).getName());
    }

    @DirtiesContext
    @Test
    void getItemById_whenItemNotFound_thenThrowsException() {
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(22));
    }

    @DirtiesContext
    @Test
    void getItemsByOwnerWithoutBookings_thenReturnList() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@ya.ru");
        userController.createUser(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("descrip");
        item.setAvailable(true);

        itemService.addItem(1L, item);

        assertEquals(itemService.getItemsByOwner(1L).get(0).getDescription(), "descrip");
    }

    @DirtiesContext
    @Test
    void getItemsByOwnerWithBookings_thenReturnList() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@ya.ru");
        userController.createUser(user);

        UserDto booker = new UserDto();
        booker.setName("booker");
        booker.setEmail("booker@mail.ru");
        userController.createUser(booker);

        Item item = new Item();
        item.setName("item");
        item.setDescription("descrip");
        item.setAvailable(true);
        itemService.addItem(1L, item);

        BookingDto booking = new BookingDto();
        booking.setItemId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        bookingController.addBooking(2L, booking);

        bookingService.addResponseToBooking(1L, 1L, true);
        assertEquals(bookingService.getAllBookingsForItemsOfOwner(1L, "ALL", PageRequest.of(0, 10)).size(), 1);
        assertEquals(itemService.getItemsByOwner(1L).get(0).getDescription(), "descrip");
    }

    @Test
    void searchItem_whenTextIsEmpty_thenReturnEmptyList() {
        List<Item> expectedResult = new ArrayList<>();
        String text = "";
        List<Item> result = itemService.searchItem(text);

        assertEquals(result, expectedResult);
    }

    @DirtiesContext
    @Test
    void searchItem_whenTextExists_thenReturnList() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@ya.ru");
        userController.createUser(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("descrip");
        item.setAvailable(true);
        itemService.addItem(1L, item);

        List<Item> expectedResult = new ArrayList<>();
        expectedResult.add(item);

        String text = "item";
        List<Item> result = itemService.searchItem(text);

        assertEquals(result, expectedResult);
    }

    @Test
    void checkItemOwnerTest_whenOwnerIsNull_thenThrowsException() {

        assertThrows(UserNotFoundException.class,
                () -> itemService.checkItemOwner(1, new Item()));
    }

    @DirtiesContext
    @Test
    void checkAndSetFieldsForUpdate() {
        UserDto user = new UserDto();
        user.setName("user");
        user.setEmail("user@ya.ru");
        userController.createUser(user);

        Item updatedItem = new Item();
        updatedItem.setName("item");
        updatedItem.setDescription("descrip");
        updatedItem.setAvailable(true);
        itemService.addItem(1L, updatedItem);

        Item newItem = new Item();
        newItem.setDescription("new_description");
        newItem.setAvailable(false);

        assertEquals(
                itemService.checkAndSetFieldsForUpdate(newItem, 1L).getName(),
                "item");
    }
}