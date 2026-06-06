package ru.practicum.shareit.excepiton;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({UserController.class, ItemController.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ExceptionHandlerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    @Test
    void handleNotFoundException() throws Exception {
        long userId = 1;
        String errorMessage = "Пользователь с id=" + userId + " не найден";

        when(userService.getById(userId))
                .thenThrow(new NotFoundException(errorMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{itemId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Объект не найден"))
                .andExpect(jsonPath("$.details").value(errorMessage));
    }

    @Test
    void handleEmailAlreadyExistsException() throws Exception {
        long userId = 1;
        UpdateUserDto dto = new UpdateUserDto(
                "test@example.com",
                "test name"
        );

        String errorMessage = "Пользователь с таким email уже существует";
        when(userService.update(any(UpdateUserDto.class), anyLong()))
                .thenThrow(new EmailAlreadyExistsException(errorMessage));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Конфликт данных"))
                .andExpect(jsonPath("$.details").value(errorMessage));
    }

    @Test
    void handleAccessDeniedException() throws Exception {
        long userId = 1;
        long itemId = 1;
        var dto = new UpdateItemDto(
                "new name",
                "new desc",
                true
        );
        var errorMessage = String.format(
                "Пользователь с id=%d не является владельцем вещи с id=%d", userId, itemId
        );

        when(itemService.update(any(UpdateItemDto.class), anyLong(), anyLong()))
                .thenThrow(new AccessDeniedException(errorMessage));

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Доступ запрещен"))
                .andExpect(jsonPath("$.details").value(errorMessage));
    }

    @Test
    void handleInternalServerError() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        var errorMessage = String.format("Предмет с id=%d не был бронирован пользователем с id=%d", itemId, userId);
        when(itemService.addComment(any(CreateCommentDto.class), anyLong(), anyLong()))
                .thenThrow(new ConditionsNotMetException(errorMessage));
        var dto = new CreateCommentDto("Test Text");

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Внутренняя ошибка сервера"))
                .andExpect(jsonPath("$.details").value(errorMessage));
    }
}
