package ru.practicum.shareit.request.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.dto.RequestItemInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ItemRequestWithItemsDtoMapperTest {

    private ItemRequest entity;
    private User user;
    private Set<Item> items;
    private LocalDateTime created;

    private final long item1Id = 101;
    private final long item2Id = 201;
    private final long userId = 1;
    private final long requestId = 1;

    @BeforeEach
    void setup() {
        created = LocalDateTime.now();

        user = new User();
        user.setId(userId);
        user.setEmail("test@mail.com");
        user.setName("test name");

        Item item1 = new Item();
        item1.setId(item1Id);
        item1.setName("Item 1");
        item1.setAvailable(true);
        item1.setDescription("test desc 1");
        item1.setOwner(user);

        Item item2 = new Item();
        item2.setId(item2Id);
        item1.setAvailable(true);
        item1.setDescription("test desc 2");
        item2.setName("Item 2");
        item2.setOwner(user);

        items = Set.of(item1, item2);

        entity = new ItemRequest();
        entity.setId(requestId);
        entity.setDescription("Test description");
        entity.setUser(user);
        entity.setCreated(created);
        entity.setItems(items);
    }

    @Test
    void mapToDto_ShouldMapItemRequestEntityToDtoCorrectly() {
        List<RequestItemInfoDto> expectedItems = List.of(
                new RequestItemInfoDto(item1Id, "Item 1", userId),
                new RequestItemInfoDto(item2Id, "Item 2", userId)
        );
        ItemRequestWithItemsDto result = ItemRequestWithItemsDtoMapper.mapToDto(entity);

        assertThat(result.getId(), is(requestId));
        assertThat(result.getDescription(), is("Test description"));
        assertThat(result.getUserId(), is(userId));
        assertThat(result.getCreated(), is(created));
        assertThat(result.getItems(), containsInAnyOrder(
                hasProperty("itemId", is(item1Id)),
                hasProperty("itemId", is(item2Id))
        ));
    }

    @Test
    void mapToDtoSetOfItems_ShouldMapAllItemsCorrectly() {
        List<RequestItemInfoDto> result = ItemRequestWithItemsDtoMapper.mapToDto(items);

        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(
                allOf(
                        hasProperty("itemId", is(item1Id)),
                        hasProperty("name", is("Item 1")),
                        hasProperty("ownerId", is(userId))
                ),
                allOf(
                        hasProperty("itemId", is(item2Id)),
                        hasProperty("name", is("Item 2")),
                        hasProperty("ownerId", is(userId))
                )
        ));
    }

    @Test
    void mapToDtoItem_ShouldMapSingleItemEntityToDtoCorrectly() {
        Item item = new Item();
        item.setId(999L);
        item.setName("Single Item");
        item.setOwner(user);

        RequestItemInfoDto result = ItemRequestWithItemsDtoMapper.mapToDto(item);

        assertThat(result.getItemId(), is(999L));
        assertThat(result.getName(), is("Single Item"));
        assertThat(result.getOwnerId(), is(userId));
    }

}