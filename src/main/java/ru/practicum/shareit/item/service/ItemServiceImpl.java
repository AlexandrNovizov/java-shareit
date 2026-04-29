package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(CreateItemDto newItem, Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId))
        );

        Item createdItem = itemRepository.create(ItemDtoMapper.mapToItem(newItem, ownerId));

        return ItemDtoMapper.mapToDto(createdItem);
    }

    @Override
    public ItemDto update(UpdateItemDto updateItemDto, Long itemId, Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId))
        );

        ItemDto oldItem = getById(itemId);

        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException(
                    String.format(
                            "Пользователь с id=%d не является владельцем вещи с id=%d", ownerId, itemId
                    )
            );
        }

        oldItem.setName(updateItemDto.getName());
        oldItem.setDescription(updateItemDto.getDescription());
        oldItem.setAvailable(updateItemDto.getAvailable());

        Item updatedItem = itemRepository.update(ItemDtoMapper.mapToItem(oldItem));

        return ItemDtoMapper.mapToDto(updatedItem);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Предмет с id=%d не найден", itemId))
        );

        return ItemDtoMapper.mapToDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwnerId(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId))
        );

        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String query) {
        if (query.isEmpty()) {
            return List.of();
        }

        return itemRepository.search(query.toLowerCase()).stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();
    }
}
