package ru.practicum.shareit.exception;

public class BookingStateNotFoundException extends RuntimeException {

    public BookingStateNotFoundException(String message) {
        super(message);
    }
}
