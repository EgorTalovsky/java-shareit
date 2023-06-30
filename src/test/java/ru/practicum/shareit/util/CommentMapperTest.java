package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    @Test
    void toCommentDtoTest() {
        User author = new User();
        author.setId(1);

        Item item = new Item();
        item.setId(1);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("comment");
        comment.setAuthor(author);
        comment.setItem(item);

        assertEquals(CommentMapper.toCommentDto(comment).getText(), "comment");
    }
}