package ru.practicum.shareit.validate;

public class PageValidator {

    public static void checkPageable(Integer from, Integer size) {
        if (size < 0 || from < 0) {
            throw new PageException("ошибка пагинации");
        }
    }
}
