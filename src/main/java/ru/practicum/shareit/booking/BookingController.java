package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.PageChecker.checkPageable;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public Booking addBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                              @RequestBody @Valid BookingDto bookingDto) {
        Item item = ItemMapper.toItem(itemService.getItemById(bookingDto.getItemId()));
        Booking booking = BookingMapper.toBooking(bookingDto, item);
        return bookingService.addBooking(bookerId, booking, item);
    }

    @PatchMapping("/{bookingId}")
    public Booking addResponseToBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long bookingId,
                                        @RequestParam Boolean approved) {
        return bookingService.addResponseToBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getAllBookingsForUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        checkPageable(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.getAllUserBookings(userId, state, pageable);
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingsForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        checkPageable(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.getAllBookingsForItemsOfOwner(userId, state, pageable);
    }
}
