package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl {
    private final UserController userController;
    private final BookingRepository bookingRepository;


    public Booking addBooking(long bookerId, Booking booking, Item item) {
        if (item.getOwner().getId() == bookerId) {
            throw new BookingNotFoundException("Вы не можете забронировать свою же вещь");
        }
        if (item.getAvailable().equals(false)) {
            throw new ItemUnavailableException("Вещь недоступна");
        }
        if (userController.getUserById(bookerId) == null) {
            throw new UserNotFoundException("Арендатор не найден");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingCheckException("Начало и окончание аренды не могут быть в прошлом");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().equals(booking.getEnd())) {
            throw new BookingCheckException("Окончание аренды не может быть одновременно с началом либо раньше его");
        }
        booking.setBooker(userController.getUserById(bookerId));
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    public Booking addResponseToBooking(long userId, long bookingId, Boolean status) {
        Booking booking = getBookingById(userId, bookingId);
        if (userId == booking.getBooker().getId()) {
            throw new BookingNotFoundException("Вы не владелец вещи, а всего лишь машина, только имитация жизни");
        }
        if (status.equals(true)) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new BookingCheckException("Статус вещи уже установлен как \"доступный\"");
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с таким id не нашлось"));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return booking;
        } else {
            throw new BookingNotFoundException("Вы не владелец вещи");
        }
    }

    public List<Booking> getAllUserBookings(long userId, String state) {
        userController.getUserById(userId);
        if (state.equals("WAITING")) {
            return sortedList(bookingRepository.findAllByStatusWaiting(userId));
        }
        if (state.equals("FUTURE")) {
            return sortedList(bookingRepository.findAllBookingsOnFuture(userId, LocalDateTime.now()));
        }
        if (state.equals("ALL")) {
            return sortedList(bookingRepository.findAllBookingsByBookerId(userId));
        }
        if (state.equals("CURRENT")) {
            return sortedList(bookingRepository.findAllCurrentBookings(userId, LocalDateTime.now()));
        }
        if (state.equals("REJECTED")) {
            return sortedList(bookingRepository.findAllRejectedBookingsForBooker(userId));
        }
        throw new BookingStateNotFoundException(state);

    }

    public List<Booking> getAllBookingsForItemsOfOwner(long ownerId, String state) {
        userController.getUserById(ownerId);
        if (state.equals("ALL")) {
            return sortedList(bookingRepository.findAllByItemOwnerId(ownerId));
        }
        if (state.equals("FUTURE")) {
            return sortedList(bookingRepository.findAllOwnerBookingsOnFuture(ownerId, LocalDateTime.now()));
        }
        if (state.equals("PAST")) {
            return sortedList(bookingRepository.findAllOwnersBookingsOnPast(ownerId, LocalDateTime.now()));
        }
        if (state.equals("REJECTED")) {
            return sortedList(bookingRepository.findAllOwnersRejectedBookings(ownerId));
        }
        if (state.equals("WAITING")) {
            return sortedList(bookingRepository.findAllWaitingBookingsForOwner(ownerId));
        }
        throw new BookingStateNotFoundException(state);
    }

    private List<Booking> sortedList(List<Booking> bookings) {
        return bookings
                .stream()
                .sorted(((o1, o2) -> o2.getStart().compareTo(o1.getStart())))
                .collect(Collectors.toList());
    }
}
