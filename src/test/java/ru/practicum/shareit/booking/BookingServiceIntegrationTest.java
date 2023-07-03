package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.BookingStateNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "shareit=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    @Autowired
    UserController userController;
    @Autowired
    UserService userService;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    BookingService bookingService;
    @Autowired
    ItemService itemService;

    @DirtiesContext
    @Test
    void addBookingByBooker_whenItIsOwner_thenThrowException() {
        long bookerId = 1L;
        Booking booking = new Booking();
        User owner = new User();
        owner.setId(bookerId);
        Item item = new Item();
        item.setOwner(owner);

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.addBooking(bookerId, booking, item));
    }

    @DirtiesContext
    @Test
    void addBooking_whenItemIsUnavailable_thenThrowException() {
        long ownerId = 1L;
        long bookerId = 2L;
        Booking booking = new Booking();

        User owner = new User();
        owner.setId(ownerId);

        User booker = new User();
        booker.setId(bookerId);

        Item item = new Item();
        item.setOwner(owner);
        item.setAvailable(false);

        assertThrows(ItemUnavailableException.class,
                () -> bookingService.addBooking(bookerId, booking, item));
    }

    @DirtiesContext
    @Test
    void addResponseToBooking_whenItIsNtApproved_thenStatusShouldBeRejected() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");
        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");
        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");
        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);

        assertEquals(bookingService.addResponseToBooking(ownerId, bookingId, false).getStatus(),
                BookingStatus.REJECTED);
    }

    @Test
    void getBookingById_whenIsNotExistId_thenThrowsException() {
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(1L, 228L));
    }

    @DirtiesContext
    @Test
    void getBookingById_forOwner_thenReturnBooking() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);

        assertEquals(bookingService.getBookingById(ownerId, bookingId).getId(), 1L);
    }

    @DirtiesContext
    @Test
    void getBookingById_whenUserIsAlien_thenThrowsException() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(228L, bookingId));
    }

    @DirtiesContext
    @Test
    void getAllUserBookings_whenStateIsWaiting_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllUserBookings(bookerId, "WAITING", pageable).size());
    }

    @DirtiesContext
    @Test
    void getAllUserBookings_whenStateIsNotExist_thenThrowsException() {
        long ownerId = 1L;
        long bookerId = 2L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(BookingStateNotFoundException.class,
                () -> bookingService.getAllUserBookings(bookerId, "error", pageable));
    }

    @DirtiesContext
    @Test
    void getAllUserBookings_whenStateIsFuture_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        bookingService.addResponseToBooking(ownerId, bookingId, true);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllUserBookings(bookerId, "FUTURE", pageable).size());
    }

    @DirtiesContext
    @Test
    void getAllUserBookings_whenStateIsAll_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        bookingService.addResponseToBooking(ownerId, bookingId, true);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllUserBookings(bookerId, "ALL", pageable).size());
    }

    @DirtiesContext
    @SneakyThrows
    @Test
    void getAllUserBookings_whenStateIsCurrent_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusSeconds(1));

        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        TimeUnit.SECONDS.sleep(1);
        bookingService.addResponseToBooking(ownerId, bookingId, true);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllUserBookings(bookerId, "CURRENT", pageable).size());
    }

    @DirtiesContext
    @Test
    void getAllUserBookings_whenStateIsRejected_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        bookingService.addResponseToBooking(ownerId, bookingId, false);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllUserBookings(bookerId, "REJECTED", pageable).size());
    }

    @DirtiesContext
    @SneakyThrows
    @Test
    void getAllUserBookings_whenStateIsPast_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusSeconds(2));
        booking.setStart(LocalDateTime.now().plusSeconds(1));

        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        TimeUnit.SECONDS.sleep(2);
        bookingService.addResponseToBooking(ownerId, bookingId, true);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllUserBookings(bookerId, "PAST", pageable).size());
    }


    @DirtiesContext
    @Test
    void getAllBookingsForItemsOfOwner_whenStateIsAll_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        bookingService.addResponseToBooking(ownerId, bookingId, true);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllBookingsForItemsOfOwner(ownerId, "ALL", pageable).size());
    }

    @DirtiesContext
    @Test
    void getAllBookingsForItemsOfOwner_whenStateIsNotExist_thenThrowsException() {
        long ownerId = 1L;
        long bookerId = 2L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);

        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(BookingStateNotFoundException.class,
                () -> bookingService.getAllBookingsForItemsOfOwner(ownerId, "error", pageable));
    }

    @DirtiesContext
    @Test
    void getAllBookingsForItemsOfOwner_whenStateIsWaiting_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllBookingsForItemsOfOwner(ownerId, "WAITING", pageable).size());
    }

    @DirtiesContext
    @Test
    void getAllBookingsForItemsOfOwner_whenStateIsFuture_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        bookingService.addResponseToBooking(ownerId, bookingId, true);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllBookingsForItemsOfOwner(ownerId, "FUTURE", pageable).size());
    }

    @DirtiesContext
    @SneakyThrows
    @Test
    void getAllBookingsForItemsOfOwner_whenStateIsPast_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusSeconds(2));
        booking.setStart(LocalDateTime.now().plusSeconds(1));

        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        TimeUnit.SECONDS.sleep(2);
        bookingService.addResponseToBooking(ownerId, bookingId, true);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllBookingsForItemsOfOwner(ownerId, "PAST", pageable).size());
    }

    @DirtiesContext
    @SneakyThrows
    @Test
    void getAllBookingsForItemsOfOwner_whenStateIsCurrent_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusSeconds(1));

        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        TimeUnit.SECONDS.sleep(1);
        bookingService.addResponseToBooking(ownerId, bookingId, true);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllBookingsForItemsOfOwner(ownerId, "CURRENT", pageable).size());
    }

    @DirtiesContext
    @Test
    void getAllBookingsForItemsOfOwner_whenStateIsRejected_thenReturnListWithSizeOf1() {
        long ownerId = 1L;
        long bookerId = 2L;
        long bookingId = 1L;

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@ya.ru");

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@ya.ru");

        Item item = new Item();
        item.setName("item");
        item.setDescription("foo");

        item.setAvailable(true);
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setItem(item);

        userService.createUser(owner);
        userService.createUser(booker);
        itemService.addItem(ownerId, item);
        bookingService.addBooking(bookerId, booking, item);
        bookingService.addResponseToBooking(ownerId, bookingId, false);

        Pageable pageable = PageRequest.of(0, 10);

        assertEquals(1, bookingService.getAllBookingsForItemsOfOwner(ownerId, "REJECTED", pageable).size());
    }
}