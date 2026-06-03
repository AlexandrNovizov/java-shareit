package ru.practicum.shareit.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.dto.RequestItemInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Set;

@UtilityClass
public class ItemRequestWithItemsDtoMapper {

    public static ItemRequestWithItemsDto mapToDto(ItemRequest entity) {
        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto();

        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setUserId(entity.getUser().getId());
        dto.setCreated(entity.getCreated());

        dto.setItems(ItemRequestWithItemsDtoMapper.mapToDto(entity.getItems()));

        return dto;
    }

    public static List<RequestItemInfoDto> mapToDto(List<Item> entities) {
        return entities.stream()
                .map(ItemRequestWithItemsDtoMapper::mapToDto)
                .toList();
    }

    public static RequestItemInfoDto mapToDto(Item entity) {
        RequestItemInfoDto dto = new RequestItemInfoDto();

        dto.setItemId(entity.getId());
        dto.setName(entity.getName());
        dto.setOwnerId(entity.getOwner().getId());

        return dto;
    }
}
