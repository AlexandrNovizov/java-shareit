package ru.practicum.shareit.request.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User testUser = new User(1L, "test@mail.ru", "testName");

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        String expectedDesc = "test desc";
        String expectedMessage = String.format("Пользователь с id=%d не найден", testUser.getId());
        CreateItemRequestDto testDto = new CreateItemRequestDto(expectedDesc);
        ItemRequest expectedRequest = new ItemRequest(
                1L,
                expectedDesc,
                testUser,
                LocalDateTime.now(),
                new ArrayList<>()
        );

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(expectedRequest);


        Throwable exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.create(testUser.getId(), testDto)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldReturnCreatedRequest() {
        Long expectedRequestId = 1L;
        String expectedDesc = "test desc";
        CreateItemRequestDto testDto = new CreateItemRequestDto(expectedDesc);
        ItemRequest expectedRequest = new ItemRequest(
                expectedRequestId,
                expectedDesc,
                testUser,
                LocalDateTime.now(),
                new ArrayList<>()
        );

        when(userRepository.findById(anyLong()))
                .thenAnswer(ignored -> Optional.of(testUser));

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(expectedRequest);


        ItemRequestDto savedRequest = itemRequestService.create(testUser.getId(), testDto);

        assertThat(savedRequest, Matchers.allOf(
                Matchers.hasProperty("id", Matchers.equalTo(expectedRequestId)),
                Matchers.hasProperty("description", Matchers.equalTo(expectedDesc)),
                Matchers.hasProperty("userId", Matchers.equalTo(testUser.getId()))
        ));
    }
}