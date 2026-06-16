package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, CreateItemRequestDto dto);

    List<ItemRequestWithItemsDto> getAllByOwnerId(Long ownerId);

    List<ItemRequestDto> getAll();

    ItemRequestWithItemsDto getById(Long requestId);

    ItemRequestWithItemsDto addItem(Long userId, Long requestId, Long itemId);
}
