package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto create(CreateItemDto newItem, Long ownerId);

    ItemDto update(UpdateItemDto updateItemDto, Long itemId, Long ownerId);

    ItemDto getById(Long itemId, Long userId);

    List<ItemDto> getAllByOwnerId(Long ownerId);

    List<ItemDto> search(String query);

    CommentDto addComment(CreateCommentDto newComment, Long itemId, Long userId);

}
