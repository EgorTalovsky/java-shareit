package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.impl.ItemRequestServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @MockBean
    UserService userService;
    @MockBean
    ItemRequestServiceImpl itemRequestService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;


    @SneakyThrows
    @Test
    void addItemTest_withoutRequestId_thenReturnItem() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        Item item = new Item(1, "item1", "descrip", true, user);

        when(itemService.addItem(userId, item)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).addItem(userId, item);
    }

    @SneakyThrows
    @Test
    void addItemTest_withRequestId_thenReturnItem() {
        long userId = 1L;
        ItemDto itemDto = new ItemDto(1L, "name", "descrip", true, 1L);
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setItems(new ArrayList<>());
        request.getItems().add(new Item());
        Item item = ItemMapper.toItemWithRequest(itemDto, request);

        when(itemRequestService.getFullItemRequestById(1)).thenReturn(request);
        when(itemService.addItem(userId, item)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, times(1)).addItem(userId, item);
    }


    @SneakyThrows
    @Test
    void updateItem() {
        Item item = new Item();
        item.setId(1);

        mockMvc.perform(patch("/items/{itemId}", item.getId())
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, times(1)).updateItem(item, 1, 1);
    }

    @SneakyThrows
    @Test
    void getItemByIdForOwner() {
        long userId = 1;
        long itemId = 1;
        User user = new User();
        user.setId(userId);
        Item item = new Item(itemId, "item1", "descrip", true, user);
        List<CommentDto> commentDtoList = new ArrayList<>();
        ItemWithBookingsAndCommentsDto itemDto =
                new ItemWithBookingsAndCommentsDto(2, "name", "descrip", true, null, null, commentDtoList);

        when(itemService.getItemById(itemId)).thenReturn(ItemMapper.toItemDtoWithOwner(item));
        when(itemService.getItemsByOwner(userId))
                .thenReturn(
                        List.of(new ItemWithBookingsAndCommentsDto(
                                item.getId(),
                                item.getName(),
                                item.getDescription(),
                                item.getAvailable(),
                                null,
                                null,
                                commentDtoList), itemDto));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItemById(itemId);
    }

    @SneakyThrows
    @Test
    void getItemByIdForUserTest() {
        long userId = 1;
        long itemId = 1;
        User user = new User();
        user.setId(userId);
        Item item = new Item(itemId, "item1", "descrip", true, user);
        List<CommentDto> commentDtoList = new ArrayList<>();

        when(itemService.getItemById(itemId)).thenReturn(ItemMapper.toItemDtoWithOwner(item));
        when(itemService.getItemsByOwner(userId))
                .thenReturn(
                        List.of(new ItemWithBookingsAndCommentsDto(
                                item.getId(),
                                item.getName(),
                                item.getDescription(),
                                item.getAvailable(),
                                null,
                                null,
                                commentDtoList)));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItemById(itemId);
    }

    @SneakyThrows
    @Test
    void getAllItemsByOwnerTest() {
        long userId = 1;
        long itemId = 1;
        User user = new User();
        user.setId(userId);
        Item item = new Item(itemId, "item1", "descrip", true, user);
        List<CommentDto> commentDtoList = new ArrayList<>();

        List<ItemWithBookingsAndCommentsDto> result = List.of(
                new ItemWithBookingsAndCommentsDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getAvailable(),
                        null,
                        null,
                        commentDtoList));

        when(itemService.getItemsByOwner(userId)).thenReturn(result);

        String response = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(response, objectMapper.writeValueAsString(result));
    }

    @SneakyThrows
    @Test
    void searchItemTest() {
        String text = "item";

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk());

        verify(itemService, times(1)).searchItem("item");
    }

    @SneakyThrows
    @Test
    void addCommentTest() {
        long itemId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");
        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);

        when(itemService.getItemById(itemId)).thenReturn(itemDto);
        when(itemService.addComment(any(Comment.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());

        verify(itemService, times(1)).addComment(any(Comment.class));
    }
}