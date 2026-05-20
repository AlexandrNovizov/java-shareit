package ru.practicum.shareit.item.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.projection.LastAndNextBooking;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ItemDtoMapper {

    public static ItemDto mapToDto(Item entity) {
        ItemDto dto = new ItemDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setAvailable(entity.getAvailable());

        return dto;
    }

    public static Item mapToItem(CreateItemDto dto, User owner) {
        Item entity = new Item();

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAvailable(dto.getAvailable());
        entity.setOwner(owner);

        return entity;
    }

    public static Item mapToItem(ItemDto dto, User owner) {
        Item entity = new Item();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAvailable(dto.getAvailable());
        entity.setOwner(owner);

        return entity;
    }

    public static List<ItemDto> setBookingInfo(
            Collection<ItemDto> items,
            Map<Long, LastAndNextBooking> dateTimeInfoMap) {

        return items.stream()
                .map(item -> setBookingInfo(item, dateTimeInfoMap.get(item.getId())))
                .toList();
    }

    public static ItemDto setBookingInfo(ItemDto dto, LastAndNextBooking info) {
        if (info == null) {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
        } else {
            dto.setLastBooking(info.getLast());
            dto.setNextBooking(info.getNext());
        }
        return dto;
    }
}
