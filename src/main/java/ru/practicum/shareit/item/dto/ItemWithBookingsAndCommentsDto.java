package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingSimplifiedDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemWithBookingsAndCommentsDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingSimplifiedDto lastBooking;
    private BookingSimplifiedDto nextBooking;
    private List<CommentDto> comments;
}
