package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotNull
    @NotEmpty
    private String name;
    @NotEmpty
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private List<CommentDto> comments;

}
