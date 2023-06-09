package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private long id;
    @NotNull(message = "Укажите время начала аренды")
    private LocalDateTime start;
    @NotNull(message = "Укажите время окончания аренды")
    private LocalDateTime end;
    private long itemId;
    private User booker;
    private BookingStatus status;
}
