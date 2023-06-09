package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {

    Booking addBooking(long bookerId, Booking booking, Item item);

    Booking addResponseToBooking(long userId, long bookingId, Boolean status);

    Booking getBookingById(long userId, long bookingId);

    List<Booking> getAllUserBookings(long userId, String state, Pageable pageable);

    List<Booking> getAllBookingsForItemsOfOwner(long ownerId, String state, Pageable pageable);

}
