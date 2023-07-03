package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.impl.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingCheckException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    UserController userController;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    BookingServiceImpl bookingService;
    long ownerId = 1L;
    long bookerId = 2L;
    long bookingId = 1L;
    Booking booking = new Booking();
    User owner = new User();
    User booker = new User();
    Item item = new Item();


    @Test
    void addBooking_whenUserNotFound_thenThrowException() {
        owner.setId(ownerId);
        item.setOwner(owner);
        item.setAvailable(true);

        when(userController.getUserById(bookerId)).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.addBooking(bookerId, booking, item));
    }

    @Test
    void addBooking_whenEndBeforeNow_thenThrowException() {
        booking.setEnd(LocalDateTime.now().minusDays(1));
        owner.setId(ownerId);
        booker.setId(bookerId);
        item.setOwner(owner);
        item.setAvailable(true);

        when(userController.getUserById(bookerId)).thenReturn(booker);

        assertThrows(BookingCheckException.class,
                () -> bookingService.addBooking(bookerId, booking, item));
    }

    @Test
    void addBooking_whenStartBeforeNow_thenThrowException() {
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStart(LocalDateTime.now().minusDays(1));
        owner.setId(ownerId);
        booker.setId(bookerId);
        item.setOwner(owner);
        item.setAvailable(true);

        when(userController.getUserById(bookerId)).thenReturn(booker);

        assertThrows(BookingCheckException.class,
                () -> bookingService.addBooking(bookerId, booking, item));
    }

    @Test
    void addBooking_whenEndBeforeStart_thenThrowException() {
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStart(LocalDateTime.now().plusDays(2));
        owner.setId(ownerId);
        booker.setId(bookerId);
        item.setOwner(owner);
        item.setAvailable(true);

        when(userController.getUserById(bookerId)).thenReturn(booker);

        assertThrows(BookingCheckException.class,
                () -> bookingService.addBooking(bookerId, booking, item));
    }

    @Test
    void addBooking_whenEndEqualsStart_thenThrowException() {
        booking.setEnd(LocalDateTime.of(2023, 7, 13, 12, 0));
        booking.setStart(LocalDateTime.of(2023, 7, 13, 12, 0));
        owner.setId(ownerId);
        booker.setId(bookerId);
        item.setOwner(owner);
        item.setAvailable(true);

        when(userController.getUserById(bookerId)).thenReturn(booker);

        assertThrows(BookingCheckException.class,
                () -> bookingService.addBooking(bookerId, booking, item));
    }

    @Test
    void addResponseToBooking_whenBookerTryAddResponse_thenThrowsException() {
        booking.setId(bookingId);
        owner.setId(ownerId);
        booker.setId(ownerId);
        booking.setBooker(booker);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.addResponseToBooking(ownerId, bookingId, true));
    }

    @Test
    void addResponseToBooking_whenStatusIsApproved_thenThrowsException() {
        booking.setId(bookingId);
        owner.setId(ownerId);
        booker.setId(bookerId);
        booking.setBooker(booker);
        item.setOwner(owner);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(BookingCheckException.class,
                () -> bookingService.addResponseToBooking(ownerId, bookingId, true));
    }


    @Test
    void getAllUserBookings() {
    }

    @Test
    void getAllBookingsForItemsOfOwner() {
    }
}