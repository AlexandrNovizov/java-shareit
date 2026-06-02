package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User user = new User(
            null,
            "test@mail.com",
            "testName"
    );

    @Test
    void shouldSaveUser() {
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
    void shouldReturnRequestsOfUserWithGivenId() {
        User otherUser = new User(null, "other@mail.ru", "otherName");

        List<ItemRequest> testUserRequests = List.of(
                new ItemRequest(null, "test user 1", user, LocalDateTime.now().minusSeconds(1), List.of()),
                new ItemRequest(null, "test user 2", user, LocalDateTime.now().minusSeconds(5), List.of())
        );

        List<ItemRequest> otherUserRequests = List.of(
                new ItemRequest(null, "other user 1", otherUser, LocalDateTime.now(), List.of())
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
                new ItemRequest(null, "test user 1", user, LocalDateTime.now().minusSeconds(10), List.of()),
                new ItemRequest(null, "test user 2", user, LocalDateTime.now().minusSeconds(5), List.of())
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
}
