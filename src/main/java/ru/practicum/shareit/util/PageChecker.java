package ru.practicum.shareit.util;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.BookingStateNotFoundException;

@Component
public class PageChecker {

    public static void checkPageable(Integer from, Integer size) {
        if (size < 0 || from < 0) {
            throw new BookingStateNotFoundException("ошибка пагинации");
        }
    }
}
