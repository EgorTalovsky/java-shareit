package ru.practicum.shareit.booking.dto.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

@Component
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                bookingDto.getBooker(),
                bookingDto.getStatus());
    }
}
