package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private final long itemId = 1L;
    private final long userId = 1L;

    private CreateItemDto createItemDto;
    private UpdateItemDto updateItemDto;
    private CreateCommentDto createCommentDto;

    @BeforeEach
    void setUp() {
        createItemDto = new CreateItemDto(
                "New Item",
                "New Desc",
                true,
                null
        );
        updateItemDto = new UpdateItemDto(
                "Updated Name",
                "Updated Desc",
                true
        );
        createCommentDto = new CreateCommentDto(
                "Comment text"
        );
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(itemClient.getItem(userId, itemId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItem(userId, itemId);
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        when(itemClient.createItem(userId, createItemDto)).thenReturn(ResponseEntity.created(null).build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemDto)))
                .andExpect(status().isCreated());

        verify(itemClient, times(1)).createItem(userId, createItemDto);
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        when(itemClient.updateItem(userId, itemId, updateItemDto)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).updateItem(userId, itemId, updateItemDto);
    }

    @Test
    void getAllByOwnerId_shouldReturnOk() throws Exception {
        when(itemClient.getAllByOwnerId(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getAllByOwnerId(userId);
    }

    @Test
    void searchByQuery_shouldReturnOk() throws Exception {
        String testQuery = "test query";
        when(itemClient.search(testQuery)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", testQuery))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).search(testQuery);
    }

    @Test
    void addComment_shouldReturnCreated() throws Exception {
        when(itemClient.addComment(userId, itemId, createCommentDto)).thenReturn(ResponseEntity.created(null).build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isCreated());

        verify(itemClient, times(1)).addComment(userId, itemId, createCommentDto);
    }
}