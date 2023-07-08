package ru.practicum.shareit.validate.exception;

public class IncorrectEmailException extends RuntimeException {

    public IncorrectEmailException(String message) {
        super(message);
    }
}
