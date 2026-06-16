package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.common.EntityUtils;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityUtils entityUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;
    private User user;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto("Test Name", "test@example.com");
        updateUserDto = new UpdateUserDto("Updated Name", "updated@example.com");
        user = new User(1L, "Test Name", "test@example.com");
    }

    @Test
    void create_ShouldSaveUserAndReturnUserDto_WhenEmailIsUnique() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(createUserDto);

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(any(User.class));

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo(createUserDto.getName()));
        assertThat(result.getEmail(), equalTo(createUserDto.getEmail()));
    }

    @Test
    void create_ShouldThrowEmailAlreadyExistsException_WhenEmailExists() {
        String expectedMessage = "Пользователь с таким email уже существует";
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        Throwable exception = assertThrows(EmailAlreadyExistsException.class,
                () -> userService.create(createUserDto));

        verify(userRepository, never()).save(any(User.class));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void update_ShouldUpdateUserAndReturnUserDto_WhenUserExistsAndEmailIsUnique() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(updateUserDto, userId);

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(updateUserDto.getEmail());
        verify(userRepository).save(user);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(userId));
        assertThat(result.getName(), equalTo(updateUserDto.getName()));
        assertThat(result.getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        Long unExistentId = 999L;
        String expectedMessage = String.format("Пользователь с id=%d не найден(-о)", unExistentId);
        when(userRepository.findById(unExistentId)).thenReturn(java.util.Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class,
                () -> userService.update(updateUserDto, unExistentId));

        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void delete_ShouldCallDeleteById_WhenIdIsValid() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void getById_ShouldReturnUserDto_WhenUserExists() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        UserDto result = userService.getById(userId);

        verify(userRepository).findById(userId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(userId));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        Long unExistentId = 999L;
        String expectedMessage = String.format("Пользователь с id=%d не найден(-о)", unExistentId);
        when(userRepository.findById(unExistentId)).thenReturn(java.util.Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class, () -> userService.getById(unExistentId));
        verify(userRepository).findById(unExistentId);
        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }
}
