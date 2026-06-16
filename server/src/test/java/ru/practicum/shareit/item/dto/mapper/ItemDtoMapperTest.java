package ru.practicum.shareit.item.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.projection.LastAndNextBooking;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.Map;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class ItemDtoMapperTest {

    private Item item;
    private ItemDto itemDto;
    private CreateItemDto createItemDto;
    private User owner;
    private LastAndNextBooking lastAndNextBooking;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        createItemDto = new CreateItemDto();
        createItemDto.setName("New Item");
        createItemDto.setDescription("New Description");
        createItemDto.setAvailable(false);

        lastAndNextBooking = new LastAndNextBooking(
                item.getId(),
                null,
                null
        );
    }

    @Test
    void mapToDto_shouldMapItemToItemDto() {
        ItemDto result = ItemDtoMapper.mapToDto(item);

        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void mapToItem_fromCreateItemDto_shouldMapCreateItemDtoToItemWithOwner() {
        Item result = ItemDtoMapper.mapToItem(createItemDto, owner);

        assertThat(result.getName(), equalTo(createItemDto.getName()));
        assertThat(result.getDescription(), equalTo(createItemDto.getDescription()));
        assertThat(result.getAvailable(), equalTo(createItemDto.getAvailable()));
        assertThat(result.getOwner(), equalTo(owner));
        assertNull(result.getId());
    }

    @Test
    void mapToItem_fromItemDto_shouldMapItemDtoToItemWithOwnerAndId() {
        Item result = ItemDtoMapper.mapToItem(itemDto, owner);

        assertThat(result.getId(), equalTo(itemDto.getId()));
        assertThat(result.getName(), equalTo(itemDto.getName()));
        assertThat(result.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(result.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(result.getOwner(), equalTo(owner));
    }

    @Test
    void setBookingInfo_withNullInfo_shouldSetNullToLastAndNextBooking() {
        ItemDto result = ItemDtoMapper.setBookingInfo(itemDto, null);

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void setBookingInfo_collection_shouldProcessAllItems() {
        List<ItemDto> items = List.of(itemDto);
        Map<Long, LastAndNextBooking> dateTimeInfoMap = Map.of(itemDto.getId(), lastAndNextBooking);

        List<ItemDto> result = ItemDtoMapper.setBookingInfo(items, dateTimeInfoMap);

        assertThat(result, hasSize(1));
        ItemDto processedItem = result.get(0);
        assertThat(processedItem.getLastBooking(), equalTo(lastAndNextBooking.getLast()));
        assertThat(processedItem.getNextBooking(), equalTo(lastAndNextBooking.getNext()));
    }

    @Test
    void setBookingInfo_emptyCollection_shouldReturnEmptyList() {
        List<ItemDto> result = ItemDtoMapper.setBookingInfo(Collections.emptyList(), Collections.emptyMap());

        assertThat(result, is(empty()));
    }
}