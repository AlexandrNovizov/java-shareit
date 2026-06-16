package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    @MockBean
    private ItemServiceImpl itemService;

    private final long itemId = 1L;
    private final long userId = 1L;
    private final long ownerId = 2L;
    private final long commentId = 1L;
    private final String testQuery = "test query";

    private ItemDto item;
    private CreateItemDto newItem;
    private UpdateItemDto updatedItem;
    private CommentDto comment;
    private CreateCommentDto newComment;

    @BeforeEach
    void setUp() {
        item = new ItemDto(
                itemId,
                "Test Item",
                "Test Description",
                true,
                null,
                null,
                List.of()
        );

        newItem = new CreateItemDto(
                "Test Item",
                "Test Description",
                true,
                null
        );

        updatedItem = new UpdateItemDto(
                "Updated Item",
                "Updated Description",
                true
        );

        comment = new CommentDto(
                commentId,
                "Test Comment",
                "CommentAuthor",
                LocalDateTime.now()
        );

        newComment = new CreateCommentDto(
                "New Comment"
        );
    }

    @Test
    void getById_ShouldReturnItemDto() throws Exception {
        when(itemService.getById(itemId, userId)).thenReturn(item);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(newItem.getName()))
                .andExpect(jsonPath("$.description").value(newItem.getDescription()));

        verify(itemService).getById(itemId, userId);
    }

    @Test
    void create_ShouldCreateItem() throws Exception {
        when(itemService.create(newItem, ownerId)).thenReturn(item);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(newItem.getName()))
                .andExpect(jsonPath("$.description").value(newItem.getDescription()));

        verify(itemService).create(newItem, ownerId);
    }

    @Test
    void update_ShouldUpdateItem() throws Exception {
        ItemDto expectedItem = new ItemDto(
                item.getId(),
                updatedItem.getName(),
                updatedItem.getDescription(),
                updatedItem.getAvailable(),
                item.getLastBooking(),
                item.getNextBooking(),
                item.getComments()
        );

        when(itemService.update(updatedItem, itemId, ownerId)).thenReturn(expectedItem);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(updatedItem.getName()))
                .andExpect(jsonPath("$.description").value(updatedItem.getDescription()))
                .andExpect(jsonPath("$.available").value(updatedItem.getAvailable()));

        verify(itemService).update(updatedItem, itemId, ownerId);
    }

    @Test
    void getAllByOwnerId_ShouldReturnListOfItems() throws Exception {
        List<ItemDto> items = List.of(item);
        when(itemService.getAllByOwnerId(ownerId)).thenReturn(items);

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value(item.getName()));

        verify(itemService).getAllByOwnerId(ownerId);
    }

    @Test
    void searchByQuery_ShouldReturnItemsByQuery() throws Exception {
        List<ItemDto> items = List.of(item);
        when(itemService.search(testQuery)).thenReturn(items);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", testQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value(item.getName()));

        verify(itemService).search(testQuery);
    }

    @Test
    void addComment_ShouldAddCommentToItem() throws Exception {
        when(itemService.addComment(newComment, itemId, ownerId)).thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment))
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()));

        verify(itemService).addComment(newComment, itemId, ownerId);
    }
}
