package ru.practicum.shareit.request.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

class ItemRequestDtoMapperTest {

    private User user;
    private CreateItemRequestDto createItemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);

        createItemRequestDto = new CreateItemRequestDto();
        createItemRequestDto.setDescription("Test description");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test description");
        itemRequest.setUser(user);
        itemRequest.setCreated(java.time.LocalDateTime.now());
    }

    @Test
    void mapToEntity_ShouldMapDtoToEntity_WithCorrectFields() {
        ItemRequest result = ItemRequestDtoMapper.mapToEntity(createItemRequestDto, user);

        assertThat(result.getUser(), equalTo(user));
        assertThat(result.getDescription(), equalTo(createItemRequestDto.getDescription()));
    }

    @Test
    void mapToDto_ShouldMapEntityToDto_WithCorrectFields() {
        ItemRequestDto result = ItemRequestDtoMapper.mapToDto(itemRequest);

        assertThat(result.getId(), equalTo(itemRequest.getId()));
        assertThat(result.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(result.getUserId(), equalTo(itemRequest.getUser().getId()));
        assertThat(result.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void mapToEntity_WhenDtoHasNullDescription_ShouldSetNullDescriptionInEntity() {
        createItemRequestDto.setDescription(null);

        ItemRequest result = ItemRequestDtoMapper.mapToEntity(createItemRequestDto, user);

        assertThat(result.getDescription(), nullValue());
    }

    @Test
    void mapToDto_WhenEntityHasNullCreatedDate_ShouldSetNullCreatedInDto() {
        itemRequest.setCreated(null);

        ItemRequestDto result = ItemRequestDtoMapper.mapToDto(itemRequest);

        assertThat(result.getCreated(), nullValue());
    }
}