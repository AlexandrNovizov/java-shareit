package ru.practicum.shareit.item.dto.mapper;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {

    public static ItemDto mapToDto(Item entity) {
        ItemDto dto = new ItemDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setAvailable(entity.getAvailable());
        dto.setOwnerId(entity.getOwnerId());

        return dto;
    }

    public static Item mapToItem(CreateItemDto dto, Long ownerId) {
        Item entity = new Item();

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAvailable(dto.getAvailable());
        entity.setOwnerId(ownerId);

        return entity;
    }

    public static Item mapToItem(ItemDto dto) {
        Item entity = new Item();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAvailable(dto.getAvailable());
        entity.setOwnerId(dto.getOwnerId());

        return entity;
    }
}
