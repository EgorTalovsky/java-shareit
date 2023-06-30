package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
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
        if (booking.getBooker().getId() == userId
                || booking.getItem().getOwner().getId() == userId) {
            return booking;
        }
        throw new BookingNotFoundException("Вы не владелец вещи");
    }

    public List<Booking> getAllUserBookings(long userId, String state, Pageable pageable) {
        userController.getUserById(userId);
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("start").descending());
        if (state.equals("WAITING")) {
            return bookingRepository.findAllByStatusWaiting(userId, page);
        }
        if (state.equals("FUTURE")) {
            return bookingRepository.findAllBookingsOnFuture(userId, LocalDateTime.now(), page);
        }
        if (state.equals("ALL")) {
            return bookingRepository.findAllBookingsByBookerId(userId, page);
        }
        if (state.equals("CURRENT")) {
            return bookingRepository.findAllCurrentBookings(userId, LocalDateTime.now(), page)
                    .stream()
                    .sorted((Comparator.comparingLong(Booking::getId)))
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            return bookingRepository.findAllRejectedBookingsForBooker(userId, page);
        }
        if (state.equals("PAST")) {
            return bookingRepository.findAllPastBookingsForBooker(userId, LocalDateTime.now(), page);
        }
        throw new BookingStateNotFoundException(state);

    }

    public List<Booking> getAllBookingsForItemsOfOwner(long ownerId, String state, Pageable pageable) {
        userController.getUserById(ownerId);
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("start").descending());
        if (state.equals("ALL")) {
            return bookingRepository.findAllByItemOwnerId(ownerId, page);
        }
        if (state.equals("FUTURE")) {
            return bookingRepository.findAllOwnerBookingsOnFuture(ownerId, LocalDateTime.now(), page);
        }
        if (state.equals("PAST")) {
            return bookingRepository.findAllOwnersBookingsOnPast(ownerId, LocalDateTime.now(), page);
        }
        if (state.equals("REJECTED")) {
            return bookingRepository.findAllOwnersRejectedBookings(ownerId, page);
        }
        if (state.equals("WAITING")) {
            return bookingRepository.findAllWaitingBookingsForOwner(ownerId, page);
        }
        if (state.equals("CURRENT")) {
            return bookingRepository.findAllCurrentBookingsForOwner(ownerId, LocalDateTime.now(), page)
                    .stream()
                    .sorted((Comparator.comparingLong(Booking::getId)))
                    .collect(Collectors.toList());
        }
        throw new BookingStateNotFoundException(state);
    }
}
