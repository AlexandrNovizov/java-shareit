package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    private User user = new User(
            1L,
            "test@mail.com",
            "testName"
    );

    @Test
    void shouldSaveUser() {
        long expectedRequestId = 1L;
        String expectedDesc = "test desc";

        CreateItemRequestDto createItemRequestDto = new CreateItemRequestDto(expectedDesc);

        em.merge(user);
        em.flush();

        itemRequestService.create(user.getId(), createItemRequestDto);

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
}
