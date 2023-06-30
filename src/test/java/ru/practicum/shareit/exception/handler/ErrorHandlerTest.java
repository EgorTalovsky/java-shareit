package ru.practicum.shareit.exception.handler;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.IncorrectFieldByItemException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {
    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleEmailAlreadyExist() {
        EmailAlreadyExistException model =
                new EmailAlreadyExistException("error");
        String exception = errorHandler.handleEmailAlreadyExist(model).toString();
        assertEquals(exception, "{Пользователь с таким адресом электронной почты уже зарегистрирован=error}");
    }

    @Test
    void handleIncorrectFieldOfItem() {
        IncorrectFieldByItemException model = new IncorrectFieldByItemException("error");
        String exception = errorHandler.handleIncorrectFieldOfItem(model).toString();
        assertEquals(exception, "{Некорректное поле=error}");
    }
}