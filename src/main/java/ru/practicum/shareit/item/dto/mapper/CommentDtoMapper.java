package ru.practicum.shareit.item.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentDtoMapper {

    public static CommentDto mapToCommentDto(Comment entity) {
        CommentDto dto = new CommentDto();

        dto.setId(entity.getId());
        dto.setText(entity.getText());
        dto.setAuthorName(entity.getUser().getName());
        dto.setCreated(entity.getCreated());

        return dto;
    }

    public static Comment mapToComment(CommentDto dto, Item item, User user) {
        Comment entity = new Comment();

        entity.setId(dto.getId());
        entity.setText(dto.getText());
        entity.setCreated(dto.getCreated());
        entity.setUser(user);
        entity.setItem(item);

        return entity;
    }

    public static Comment mapToComment(CreateCommentDto newComment, User user, Item item) {
        Comment entity = new Comment();

        entity.setText(newComment.getText());
        entity.setCreated(LocalDateTime.now());
        entity.setUser(user);
        entity.setItem(item);

        return entity;
    }
}
