package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingSimplifiedDto;

@Data
@AllArgsConstructor
public class ItemWithBookingsDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingSimplifiedDto lastBooking;
    private BookingSimplifiedDto nextBooking;
}
