package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    private final long userId = 1L;
    private final long otherUserId = 2L;

    private UserDto user;
    private CreateUserDto newUser;
    private UpdateUserDto updatedUser;

    @BeforeEach
    void setUp() {
        user = new UserDto(
                userId,
                "test@example.com",
                "Test User"
        );

        newUser = new CreateUserDto(
               "new@example.com",
                "New User"
        );

        updatedUser = new UpdateUserDto(
                "updated@example.com",
                "Updated User"
        );
    }

    @Test
    void getById_ShouldReturnUserDto_WhenUserExists() throws Exception {
        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));

        verify(userService).getById(userId);
    }

    @Test
    void createUser_ShouldCreateUserAndReturnUserDto() throws Exception {
        UserDto newUserDto = new UserDto(
                user.getId(),
                newUser.getEmail(),
                newUser.getName()
        );
        when(userService.create(any(CreateUserDto.class))).thenReturn(newUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(newUser.getName()))
                .andExpect(jsonPath("$.email").value(newUser.getEmail()));

        verify(userService).create(any(CreateUserDto.class));
    }

    @Test
    void updateUser_ShouldUpdateUserAndReturnUpdatedUserDto() throws Exception {
        UserDto updatedUserDto = new UserDto(
                user.getId(),
                updatedUser.getEmail(),
                updatedUser.getName()
        );
        when(userService.update(any(UpdateUserDto.class), eq(userId))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

        verify(userService).update(any(UpdateUserDto.class), eq(userId));
    }

    @Test
    void deleteUser_ShouldDeleteUserAndReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/{userId}", otherUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService).delete(otherUserId);
    }
}
