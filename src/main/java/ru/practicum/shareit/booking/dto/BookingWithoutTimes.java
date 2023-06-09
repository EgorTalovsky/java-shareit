package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingWithoutTimes {
    private long id;
    private long itemId;
    private User booker;
    private BookingStatus status;
}
