package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.BookingInfoItemDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(CreateItemDto newItem, Long ownerId);

    ItemDto update(UpdateItemDto updateItemDto, Long itemId, Long ownerId);

    ItemDto getById(Long itemId);

    List<BookingInfoItemDto> getAllByOwnerId(Long ownerId);

    List<ItemDto> search(String query);

}
