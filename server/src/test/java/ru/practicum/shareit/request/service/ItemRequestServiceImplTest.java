package ru.practicum.shareit.request.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestWithItemsDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User testUser = new User(1L, "test@mail.ru", "testName");

    @Test
    void userCreateShouldThrowExceptionIfUserNotFound() {
        String expectedDesc = "test desc";
        String expectedMessage = String.format("Пользователь с id=%d не найден", testUser.getId());
        CreateItemRequestDto testDto = new CreateItemRequestDto(expectedDesc);
        ItemRequest expectedRequest = new ItemRequest(
                1L,
                expectedDesc,
                testUser,
                LocalDateTime.now(),
                Set.of()
        );

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRequestRepository.save(ArgumentMatchers.any(ItemRequest.class)))
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
                Set.of()
        );

        when(userRepository.findById(anyLong()))
                .thenAnswer(ignored -> Optional.of(testUser));

        when(itemRequestRepository.save(ArgumentMatchers.any(ItemRequest.class)))
                .thenReturn(expectedRequest);


        ItemRequestDto savedRequest = itemRequestService.create(testUser.getId(), testDto);

        assertThat(savedRequest, Matchers.allOf(
                Matchers.hasProperty("id", equalTo(expectedRequestId)),
                Matchers.hasProperty("description", equalTo(expectedDesc)),
                Matchers.hasProperty("userId", equalTo(testUser.getId()))
        ));
    }

    @Test
    void shouldReturnRequestsForUser() {
        User otherUser = new User(2L, "other@mail.ru", "otherName");

        List<ItemRequest> testUserRequests = List.of(
                new ItemRequest(1L, "test user 1", testUser, LocalDateTime.now(), Set.of()),
                new ItemRequest(2L, "test user 2", testUser, LocalDateTime.now(), Set.of())
        );

        List<ItemRequest> otherUserRequests = List.of(
                new ItemRequest(3L, "other user 1", otherUser, LocalDateTime.now(), Set.of())
        );

        List<ItemRequestWithItemsDto> expectedList = testUserRequests.stream()
                .map(ItemRequestWithItemsDtoMapper::mapToDto)
                .toList();

        when(userRepository.findById(testUser.getId()))
                .thenAnswer(ignored -> Optional.of(testUser));
        when(userRepository.findById(otherUser.getId()))
                .thenAnswer(ignored -> Optional.of(otherUser));

        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(testUser.getId()))
                .thenReturn(testUserRequests);
        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(otherUser.getId()))
                .thenReturn(otherUserRequests);


        List<ItemRequestWithItemsDto> testUserResult = itemRequestService.getAllByOwnerId(testUser.getId());

        assertThat(testUserResult, Matchers.hasSize(2));
        assertThat(testUserResult, equalTo(expectedList));
    }

    @Test
    void getRequestsForUserShouldThrowExceptionIfUserNotFound() {
        String expectedMessage = String.format("Пользователь с id=%d не найден", testUser.getId());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of());

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllByOwnerId(testUser.getId()));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void shouldReturnAllRequests() {
        List<ItemRequest> requests = List.of(
                new ItemRequest(1L, "test user 1", testUser, LocalDateTime.now(), Set.of()),
                new ItemRequest(2L, "test user 2", testUser, LocalDateTime.now(), Set.of()),
                new ItemRequest(3L, "other user 1", testUser, LocalDateTime.now(), Set.of())
        );

        List<ItemRequestDto> expecetedList = List.of(
                new ItemRequestDto(1L, "test user 1", testUser.getId(), LocalDateTime.now()),
                new ItemRequestDto(2L, "test user 2", testUser.getId(), LocalDateTime.now()),
                new ItemRequestDto(3L, "other user 1", testUser.getId(), LocalDateTime.now())
        );

        when(itemRequestRepository.findAll(ArgumentMatchers.any(Sort.class)))
                .thenReturn(requests);

        List<ItemRequestDto> allRequests = itemRequestService.getAll();

        assertThat(allRequests, hasSize(3));
        assertThat(allRequests, equalTo(expecetedList));
    }

    @Test
    void shouldReturnRequestWithGivenId() {
        ItemRequest request = new ItemRequest(1L, "test user 1", testUser, LocalDateTime.now(), Set.of());

        when(itemRequestRepository.findById(anyLong()))
                .thenAnswer(ignored -> Optional.of(request));

        ItemRequestWithItemsDto requestDto = itemRequestService.getById(1L);

        assertThat(requestDto, allOf(
                hasProperty("id", equalTo(request.getId())),
                hasProperty("description", equalTo(request.getDescription())),
                hasProperty("userId", equalTo(request.getUser().getId())),
                hasProperty("created", equalTo(request.getCreated())),
                hasProperty("items", empty())
        ));
    }

    @Test
    void getByIdShouldThrowNotFoundExceptionIfRequestNotFound() {

        long requestId = 1L;

        String expectedMessage = String.format("Запрос с id=%d не найден", requestId);

        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(requestId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void shouldAddItemToRequest() {
        long requestId = 1;
        long itemId = 1;
        Item existingItem = new Item(
                2L,
                "ex",
                "e",
                true,
                testUser
        );
        Set<Item> items = new HashSet<>();
        items.add(existingItem);

        ItemRequest request = new ItemRequest(requestId, "test user 1", testUser, LocalDateTime.now(), items);
        Item item = new Item(
                itemId,
                "testName",
                "test desc",
                true,
                testUser
        );

        when(userRepository.findById(anyLong()))
                .thenAnswer(ignored -> Optional.of(testUser));

        when(itemRequestRepository.findById(anyLong()))
                .thenAnswer(ignored -> Optional.of(request));

        when(itemRepository.findById(anyLong()))
                .thenAnswer(ignored -> Optional.of(item));

        ItemRequestWithItemsDto requestDto = itemRequestService.addItem(testUser.getId(), requestId, itemId);

        assertThat(requestDto.getItems(), hasSize(2));
        assertThat(requestDto.getItems(), allOf(
                hasItem(hasProperty("itemId", equalTo(item.getId()))),
                hasItem(hasProperty("itemId", equalTo(existingItem.getId())))
        ));

    }

    @Test
    void addItemShouldThrowExceptionIfRequestNotFound() {
        long requestId = 1L;
        long itemId = 1L;

        String expectedMessage = String.format("Запрос с id=%d не найден", requestId);

        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(new Item()));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(testUser));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.addItem(testUser.getId(), requestId, itemId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void addItemShouldThrowExceptionIfItemNotFound() {
        long requestId = 1L;
        long itemId = 1L;

        String expectedMessage = String.format("Предмет с id=%d не найден", requestId);

        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(new ItemRequest()));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(testUser));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.addItem(testUser.getId(), requestId, itemId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void addItemShouldThrowExceptionIfUserNotFound() {
        long requestId = 1L;
        long itemId = 1L;

        String expectedMessage = String.format("Пользователь с id=%d не найден", testUser.getId());

        when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(new ItemRequest()));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(new Item()));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.addItem(testUser.getId(), requestId, itemId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }
}