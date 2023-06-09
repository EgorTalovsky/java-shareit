package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailAlreadyExist(final EmailAlreadyExistException e) {
        return Map.of("Пользователь с таким адресом электронной почты уже зарегистрирован", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleEmailIsBlank(final IncorrectEmailException e) {
        return Map.of("Адрес электронной почты имеет некорректный формат", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(final UserNotFoundException e) {
        return Map.of("Пользователь не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleItemNotFound(final ItemNotFoundException e) {
        return Map.of("Вещь не найдена", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectFieldOfItem(final IncorrectFieldByItemException e) {
        return Map.of("Некорректное поле", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnavailableItem(final ItemUnavailableException e) {
        return Map.of("Данная вещь недоступна для аренды", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectFieldOfBooking(final BookingCheckException e) {
        return Map.of("Запрос аренды некорректен", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookingNotFound(final BookingNotFoundException e) {
        return Map.of("Запрос аренды некорректен", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBookingStateNotFound(final BookingStateNotFoundException e) {
        return Map.of("error", "Unknown state: " + e.getMessage());
    }

}
