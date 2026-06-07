package ru.practicum.shareit.item.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class CommentDtoMapperTest {

    private Comment commentEntity;
    private User user;
    private Item item;
    private CommentDto commentDto;
    private CreateCommentDto createCommentDto;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("Test User");

        item = new Item();
        item.setId(1L);

        commentEntity = new Comment();
        commentEntity.setId(1L);
        commentEntity.setText("Test comment text");
        commentEntity.setUser(user);
        commentEntity.setCreated(LocalDateTime.now());
        commentEntity.setItem(item);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment text");
        commentDto.setAuthorName("Test User");
        commentDto.setCreated(LocalDateTime.now());

        createCommentDto = new CreateCommentDto();
        createCommentDto.setText("New comment text");
    }

    @Test
    void mapToCommentDto_ShouldMapAllFieldsCorrectly() {
        CommentDto result = CommentDtoMapper.mapToCommentDto(commentEntity);

        assertThat(result.getId(), equalTo(commentEntity.getId()));
        assertThat(result.getText(), equalTo(commentEntity.getText()));
        assertThat(result.getAuthorName(), equalTo(user.getName()));
        assertThat(result.getCreated(), equalTo(commentEntity.getCreated()));
    }

    @Test
    void mapToComment_WithCommentDto_ShouldMapAllFieldsCorrectly() {
        Comment result = CommentDtoMapper.mapToComment(commentDto, item, user);

        assertThat(result.getId(), equalTo(commentDto.getId()));
        assertThat(result.getText(), equalTo(commentDto.getText()));
        assertThat(result.getCreated(), equalTo(commentDto.getCreated()));
        assertThat(result.getUser(), equalTo(user));
        assertThat(result.getItem(), equalTo(item));
    }

    @Test
    void mapToComment_WithCreateCommentDto_ShouldSetCurrentTimeAndAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();

        try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
            localDateTimeMock.when(LocalDateTime::now).thenReturn(now);

            Comment result = CommentDtoMapper.mapToComment(createCommentDto, user, item);

            assertNull(result.getId());
            assertThat(result.getText(), equalTo(createCommentDto.getText()));
            assertThat(result.getCreated(), equalTo(now));
            assertThat(result.getUser(), equalTo(user));
            assertThat(result.getItem(), equalTo(item));
        }
    }
}