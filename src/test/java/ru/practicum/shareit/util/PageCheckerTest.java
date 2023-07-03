package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BookingStateNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PageCheckerTest {

    @Test
    void checkPageableTestWithAllIncorrectParams() {
        Integer size = -1;
        Integer from = -10;

        assertThrows(BookingStateNotFoundException.class, () -> PageChecker.checkPageable(from, size));
    }

    @Test
    void checkPageableTestWithIncorrectSize() {
        Integer size = -1;
        Integer from = 10;

        assertThrows(BookingStateNotFoundException.class, () -> PageChecker.checkPageable(from, size));
    }

    @Test
    void checkPageableTestWithIncorrectFrom() {
        Integer size = 1;
        Integer from = -10;

        assertThrows(BookingStateNotFoundException.class, () -> PageChecker.checkPageable(from, size));
    }
}