package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User user = new User(
            null,
            "test@mail.com",
            "testName"
    );

    @Test
    void shouldSaveRequest() {
        String expectedDesc = "test desc";

        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto(expectedDesc);

        user = userRepository.save(user);

        long expectedRequestId = itemRequestService.create(user.getId(), createItemRequestDto).getId();

        TypedQuery<ItemRequest> query = em.createQuery("SELECT r FROM ItemRequest r WHERE r.id = :requestId", ItemRequest.class);
        ItemRequest request = query.setParameter("requestId", expectedRequestId)
                .getSingleResult();

        assertThat(request, allOf(
                hasProperty("id", equalTo(expectedRequestId)),
                hasProperty("description", equalTo(expectedDesc)),
                Matchers.hasProperty("user", Matchers.allOf(
                        Matchers.hasProperty("id", Matchers.equalTo(user.getId())),
                        Matchers.hasProperty("email", Matchers.equalTo(user.getEmail())),
                        Matchers.hasProperty("name", Matchers.equalTo(user.getName()))
                ))
        ));
    }

    @Test
    void saveShouldThrowNotFoundExceptionIfUserNotExists() {
        String expectedDesc = "test desc";
        long unExistingId = Long.MAX_VALUE;
        String expectedMessage = String.format("Пользователь с id=%d не найден", unExistingId);

        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto(expectedDesc);

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(unExistingId, createItemRequestDto));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void shouldReturnRequestsOfUserWithGivenId() {
        User otherUser = new User(null, "other@mail.ru", "otherName");

        List<ItemRequest> testUserRequests = List.of(
                new ItemRequest(null, "test user 1", user, LocalDateTime.now().minusSeconds(1), Set.of()),
                new ItemRequest(null, "test user 2", user, LocalDateTime.now().minusSeconds(5), Set.of())
        );

        List<ItemRequest> otherUserRequests = List.of(
                new ItemRequest(null, "other user 1", otherUser, LocalDateTime.now(), Set.of())
        );

        user = userRepository.save(user);
        otherUser = userRepository.save(otherUser);

        for (ItemRequest request : testUserRequests) {
            request = itemRequestRepository.save(request);
        }

        for (ItemRequest req : otherUserRequests) {
            req = itemRequestRepository.save(req);
        }

        List<ItemRequestWithItemsDto> userRequests = itemRequestService.getAllByOwnerId(user.getId());

        assertThat(userRequests, hasSize(2));

        ItemRequest sourceItemRequest = testUserRequests.get(0);

        assertThat(userRequests.get(0), allOf(
                hasProperty("id", equalTo(sourceItemRequest.getId())),
                hasProperty("description", equalTo(sourceItemRequest.getDescription())),
                hasProperty("created", equalTo(sourceItemRequest.getCreated())),
                hasProperty("userId", equalTo(sourceItemRequest.getUser().getId()))
        ));

        sourceItemRequest = testUserRequests.get(1);

        assertThat(userRequests.get(1), allOf(
                hasProperty("id", equalTo(sourceItemRequest.getId())),
                hasProperty("description", equalTo(sourceItemRequest.getDescription())),
                hasProperty("created", equalTo(sourceItemRequest.getCreated())),
                hasProperty("userId", equalTo(sourceItemRequest.getUser().getId()))
        ));
    }

    @Test
    void shouldReturnRequestsOfUserOrderedByCreationDesc() {

        List<ItemRequest> testUserRequests = List.of(
                new ItemRequest(null, "test user 1", user, LocalDateTime.now().minusSeconds(10), Set.of()),
                new ItemRequest(null, "test user 2", user, LocalDateTime.now().minusSeconds(5), Set.of())
        );

        user = userRepository.save(user);

        for (ItemRequest request : testUserRequests) {
            request = itemRequestRepository.save(request);
        }

        List<ItemRequestWithItemsDto> userRequests = itemRequestService.getAllByOwnerId(user.getId());

        assertThat(userRequests, hasSize(2));

        ItemRequest sourceItemRequest = testUserRequests.get(1);

        assertThat(userRequests.get(0), allOf(
                hasProperty("id", equalTo(sourceItemRequest.getId())),
                hasProperty("description", equalTo(sourceItemRequest.getDescription())),
                hasProperty("created", equalTo(sourceItemRequest.getCreated())),
                hasProperty("userId", equalTo(sourceItemRequest.getUser().getId()))
        ));

        sourceItemRequest = testUserRequests.get(0);

        assertThat(userRequests.get(1), allOf(
                hasProperty("id", equalTo(sourceItemRequest.getId())),
                hasProperty("description", equalTo(sourceItemRequest.getDescription())),
                hasProperty("created", equalTo(sourceItemRequest.getCreated())),
                hasProperty("userId", equalTo(sourceItemRequest.getUser().getId()))
        ));
    }

    @Test
    void getAllByOwnerIdShouldThrowNotFoundExceptionIfUserNotExists() {
        long unExistingId = Long.MAX_VALUE;
        String expectedMessage = String.format("Пользователь с id=%d не найден", unExistingId);

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllByOwnerId(unExistingId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void shouldReturnRequestWithGivenId() {
        user = userRepository.save(user);

        ItemRequest request = new ItemRequest(
                null,
                "test desc",
                user,
                LocalDateTime.now(),
                Set.of()
        );

        request = itemRequestRepository.save(request);

        ItemRequestWithItemsDto receivedRequest = itemRequestService.getById(request.getId());

        assertThat(receivedRequest, allOf(
                hasProperty("id", equalTo(request.getId())),
                hasProperty("description", equalTo(request.getDescription())),
                hasProperty("created", equalTo(request.getCreated())),
                hasProperty("userId", equalTo(request.getUser().getId())),
                hasProperty("items", empty())
        ));
    }

    @Test
    void getByIdShouldThrowNotFoundExceptionIfRequestNotExists() {
        long unExistingId = Long.MAX_VALUE;
        String expectedMessage = String.format("Запрос с id=%d не найден", unExistingId);

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(unExistingId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void shouldReturnAllRequests() {
        User otherUser = new User(null, "other@mail.ru", "otherName");

        List<ItemRequest> testUserRequests = List.of(
                new ItemRequest(null, "test user 1", user, LocalDateTime.now().minusSeconds(1), Set.of()),
                new ItemRequest(null, "test user 2", user, LocalDateTime.now().minusSeconds(5), Set.of())
        );

        List<ItemRequest> otherUserRequests = List.of(
                new ItemRequest(null, "other user 1", otherUser, LocalDateTime.now(), Set.of())
        );

        userRepository.save(user);
        userRepository.save(otherUser);

        for (ItemRequest request : testUserRequests) {
            itemRequestRepository.save(request);
        }

        for (ItemRequest req : otherUserRequests) {
            itemRequestRepository.save(req);
        }

        List<ItemRequestDto> allRequests = itemRequestService.getAll();

        assertThat(allRequests, hasSize(3));
    }

    @Test
    void shouldReturnAllRequestsOrderedByCreationDesc() {

        List<ItemRequest> testUserRequests = List.of(
                new ItemRequest(null, "test user 1", user, LocalDateTime.now().minusSeconds(1), Set.of()),
                new ItemRequest(null, "test user 2", user, LocalDateTime.now().minusSeconds(5), Set.of()),
                new ItemRequest(null, "test user 3", user, LocalDateTime.now(), Set.of())
        );

        userRepository.save(user);

        for (ItemRequest request : testUserRequests) {
            request = itemRequestRepository.save(request);
        }

        List<ItemRequestDto> allRequests = itemRequestService.getAll();

        assertThat(allRequests, hasSize(3));

        List<LocalDateTime> actualOrder = allRequests.stream()
                .map(ItemRequestDto::getCreated)
                .collect(Collectors.toList());

        List<LocalDateTime> expectedOrder = actualOrder.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        assertThat(actualOrder, equalTo(expectedOrder));
    }

    @Test
    void shouldAddItemToRequest() {
        user = userRepository.save(user);
        ItemRequest request = new ItemRequest(
                null, "test desc", user, LocalDateTime.now(), new HashSet<>()
        );

        Item item = new Item(
                null, "test item", "test item desc", true, user
        );
        request = itemRequestRepository.save(request);
        item = itemRepository.save(item);

        ItemRequestWithItemsDto result = itemRequestService.addItem(request.getId(), item.getId());

        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getItems(), hasItem(allOf(
                hasProperty("itemId", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("ownerId", equalTo(item.getOwner().getId()))
        )));
    }

    @Test
    void shouldAddItemToRequestIfRequestItemsNotEmpty() {
        user = userRepository.save(user);
        Item existingItem = new Item(
                null,
                "ex item",
                "ex item desc",
                true,
                user
        );
        existingItem = itemRepository.save(existingItem);

        Set<Item> existingItems = new HashSet<>();
        existingItems.add(existingItem);

        ItemRequest request = new ItemRequest(
                null, "test desc", user, LocalDateTime.now(), existingItems
        );

        Item item = new Item(
                null, "test item", "test item desc", true, user
        );
        request = itemRequestRepository.save(request);
        item = itemRepository.save(item);

        ItemRequestWithItemsDto result = itemRequestService.addItem(request.getId(), item.getId());

        assertThat(result.getItems(), hasSize(2));

        assertThat(result.getItems(), allOf(
                hasItem(allOf(
                        hasProperty("itemId", equalTo(item.getId())),
                        hasProperty("name", equalTo(item.getName())),
                        hasProperty("ownerId", equalTo(item.getOwner().getId()))
                )),
                hasItem(allOf(
                        hasProperty("itemId", equalTo(existingItem.getId())),
                        hasProperty("name", equalTo(existingItem.getName())),
                        hasProperty("ownerId", equalTo(existingItem.getOwner().getId()))
                ))
        ));
    }

    @Test
    void addItemShouldThrowNotFoundExceptionIfRequestNotExists() {
        long unExistingId = Long.MAX_VALUE;
        String expectedMessage = String.format("Запрос с id=%d не найден", unExistingId);
        user = userRepository.save(user);
        Item item = new Item(
                null, "test item", "test item desc", true, user
        );
        item = itemRepository.save(item);
        long itemId = item.getId();
        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.addItem(unExistingId, itemId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void addItemShouldThrowNotFoundExceptionIfItemNotExists() {
        long unExistingId = Long.MAX_VALUE;
        String expectedMessage = String.format("Предмет с id=%d не найден", unExistingId);
        user = userRepository.save(user);
        ItemRequest request = new ItemRequest(
                null,
                "test desc",
                user,
                LocalDateTime.now(),
                new HashSet<>()
        );
        request = itemRequestRepository.save(request);
        long requestId = request.getId();
        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.addItem(requestId, unExistingId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }
}
