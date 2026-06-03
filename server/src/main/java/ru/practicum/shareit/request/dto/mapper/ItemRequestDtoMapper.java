package ru.practicum.shareit.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemRequestDtoMapper {

    public static ItemRequest mapToEntity(CreateItemRequestDto dto, User user) {
        ItemRequest entity = new ItemRequest();

        entity.setUser(user);
        entity.setDescription(dto.getDescription());

        return entity;
    }

    public static ItemRequestDto mapToDto(ItemRequest entity) {
        ItemRequestDto dto = new ItemRequestDto();

        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setUserId(entity.getUser().getId());
        dto.setCreated(entity.getCreated());

        return dto;
    }


}
