package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithoutTimes;
import ru.practicum.shareit.item.model.Item;

@RequiredArgsConstructor
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
