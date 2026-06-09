package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private final long userId = 1L;
    private CreateUserDto newUser;
    private UpdateUserDto updateUser;

    @BeforeEach
    void setUp() {
        newUser = new CreateUserDto(
                "new@example.com",
                "new name"
        );
        updateUser = new UpdateUserDto(
                "updated@example.com",
                "updated name"
        );
    }

    @Test
    void getById_ShouldReturnOk() throws Exception {
        when(userClient.getUser(eq(userId))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUser(eq(userId));
    }

    @Test
    void createUser_ShouldReturnCreated() throws Exception {
        when(userClient.createUser(any(CreateUserDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser))
                )
                .andExpect(status().isCreated());

        verify(userClient).createUser(any(CreateUserDto.class));
    }

    @Test
    void updateUser_ShouldReturnOk() throws Exception {
        when(userClient.updateUser(eq(userId), any(UpdateUserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser))
                )
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(userId), any(UpdateUserDto.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
       when(userClient.deleteUser(eq(userId)))
               .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userClient).deleteUser(userId);
    }
}
