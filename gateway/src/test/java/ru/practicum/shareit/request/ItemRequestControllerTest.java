package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private final long userId = 1L;
    private final long requestId = 1L;
    private final long itemId = 1L;

    private CreateItemRequestDto newRequest;

    @BeforeEach
    void setUp() {
        newRequest = new CreateItemRequestDto(
                "New Desc"
        );
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        when(itemRequestClient.createRequest(eq(userId), any(CreateItemRequestDto.class)))
                .thenReturn(ResponseEntity.created(null).build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isCreated());

        verify(itemRequestClient).createRequest(eq(userId), any(CreateItemRequestDto.class));
    }

    @Test
    void getRequestsByOwner_shouldReturnOk() throws Exception {
        when(itemRequestClient.getAllByOwnerId(eq(userId)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllByOwnerId(eq(userId));
    }

    @Test
    void getAll_shouldReturnOk() throws Exception {
        when(itemRequestClient.getAll())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAll();
    }

    @Test
    void getById_shouldReturnOk() throws Exception {
        when(itemRequestClient.getRequest(eq(requestId)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequest(eq(requestId));
    }

    @Test
    void addItemToRequest_shouldReturnOk() throws Exception {
        when(itemRequestClient.addItem(eq(userId), eq(requestId), eq(itemId)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/requests/{requestId}/add/{itemId}", requestId, itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).addItem(eq(userId), eq(requestId), eq(itemId));
    }
}

