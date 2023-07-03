package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingStateNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.impl.ItemServiceImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserController userController;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void addItem() {
        Item item = new Item();
        item.setName("name");

        when(userController.getUserById(anyLong())).thenReturn(new User());
        when(itemRepository.save(item)).thenReturn(item);

        assertEquals("name", itemService.addItem(1L, item).getName());
    }

    @Test
    void addCommentByBooker() {
        User author = new User();
        author.setId(1);

        Item item = new Item();
        item.setId(1);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("comment");
        comment.setAuthor(author);
        comment.setItem(item);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(1));

        Pageable page = PageRequest.of(0, 10);

        when(bookingRepository.findAllBookingsByBookerId(1, page))
                .thenReturn(List.of(booking));
        when(commentRepository.save(comment)).thenReturn(comment);

        assertEquals(itemService.addComment(comment).getText(), "comment");
    }

    @Test
    void addCommentByNotBooker_thenThrowsException() {
        User author = new User();
        author.setId(1);

        Item item = new Item();
        item.setId(1);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("comment");
        comment.setAuthor(author);
        comment.setItem(item);

        Pageable page = PageRequest.of(0, 10);

        when(bookingRepository.findAllBookingsByBookerId(1, page))
                .thenReturn(new ArrayList<>());

        assertThrows(BookingStateNotFoundException.class,
                () -> itemService.addComment(comment));
    }

    @Test
    void checkItemOwnerTest_whenOwnerIsNull_thenThrowsException() {
        when(userController.getUserById(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> itemService.checkItemOwner(1, new Item()));
    }
}