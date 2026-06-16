package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.dto.RequestItemInfoDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemReqeustControllerTest {

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    private final long userId = 1L;
    private final long requestId = 2L;
    private final long itemId = 3L;

    private CreateItemRequestDto newRequest;
    private ItemRequestDto request;
    private ItemRequestWithItemsDto requestWithItems;

    @BeforeEach
    void setUp() {
        newRequest = new CreateItemRequestDto(
                "Test Desc"
        );

        request = new ItemRequestDto(
                requestId,
                newRequest.getDescription(),
                userId,
                LocalDateTime.now()
        );

        requestWithItems = new ItemRequestWithItemsDto(
                request.getId(),
                request.getDescription(),
                request.getUserId(),
                request.getCreated(),
                List.of()
        );
    }

    @Test
    void create_ShouldReturnItemRequestDto() throws Exception {
        when(itemRequestService.create(eq(userId), eq(newRequest)))
                .thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(newRequest.getDescription()));

        verify(itemRequestService).create(userId, newRequest);
    }

    @Test
    void getRequestsByOwner_ShouldReturnListOfItemRequestWithItemsDto() throws Exception {
        List<ItemRequestWithItemsDto> requests = List.of(requestWithItems);
        when(itemRequestService.getAllByOwnerId(eq(userId))).thenReturn(requests);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].description").value(requestWithItems.getDescription()))
                .andExpect(jsonPath("$[0].items").isArray());

        verify(itemRequestService).getAllByOwnerId(userId);
    }

    @Test
    void getAll_ShouldReturnListOfItemRequestDto() throws Exception {
        List<ItemRequestDto> allRequests = List.of(request);
        when(itemRequestService.getAll()).thenReturn(allRequests);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].description").value(request.getDescription()));

        verify(itemRequestService).getAll();
    }

    @Test
    void getById_ShouldReturnItemRequestWithItemsDto() throws Exception {
        when(itemRequestService.getById(eq(requestId))).thenReturn(requestWithItems);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.items").isArray());

        verify(itemRequestService).getById(requestId);
    }

    @Test
    void addItemToRequest_ShouldReturnUpdatedItemRequestWithItemsDto() throws Exception {
        RequestItemInfoDto itemInfoDto = new RequestItemInfoDto(
                itemId,
                "Item Name",
                userId
        );
        requestWithItems.setItems(List.of(itemInfoDto));

        when(itemRequestService.addItem(eq(userId), eq(requestId), eq(itemId)))
                .thenReturn(requestWithItems);

        mockMvc.perform(patch("/requests/{requestId}/add/{itemId}", requestId, itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].name").value(itemInfoDto.getName()));

        verify(itemRequestService).addItem(userId, requestId, itemId);
    }
}
