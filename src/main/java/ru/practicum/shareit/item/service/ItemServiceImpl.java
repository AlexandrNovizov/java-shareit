package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.projection.LastAndNextBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.BookingInfoItemDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(CreateItemDto newItem, Long ownerId) {
        User owner = getUserOrThrowException(ownerId);

        Item createdItem = itemRepository.save(ItemDtoMapper.mapToItem(newItem, owner));

        return ItemDtoMapper.mapToDto(createdItem);
    }

    @Override
    public ItemDto update(UpdateItemDto updateItemDto, Long itemId, Long ownerId) {
        getUserOrThrowException(ownerId);

        Item oldItem = getItemOrThrowException(itemId);
        if (!oldItem.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException(
                    String.format(
                            "Пользователь с id=%d не является владельцем вещи с id=%d", ownerId, itemId
                    )
            );
        }

        setFields(oldItem, updateItemDto);
        Item updatedItem = itemRepository.save(oldItem);

        return ItemDtoMapper.mapToDto(updatedItem);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = getItemOrThrowException(itemId);

        return ItemDtoMapper.mapToDto(item);
    }

    @Override
    public List<BookingInfoItemDto> getAllByOwnerId(Long ownerId) {
        getUserOrThrowException(ownerId);

        List<ItemDto> items = itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> ids = items.stream().map(ItemDto::getId).toList();

        List<LastAndNextBooking> bookings = bookingRepository.getLastAndNextBookingForIds(ids);

        Map<Long, LastAndNextBooking> bookingInfo = bookings
                .stream()
                .collect(Collectors.toMap(LastAndNextBooking::getId, Function.identity()));

        return ItemDtoMapper.mapToBookingInfoItemDto(items, bookingInfo);
    }

    @Override
    public List<ItemDto> search(String query) {
        if (query.isEmpty()) {
            return List.of();
        }

        return itemRepository
                .searchByQuery(query)
                .stream()
                .map(ItemDtoMapper::mapToDto)
                .toList();
    }

    private Item getItemOrThrowException(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Предмет с id=%d не найден", itemId))
        );
    }

    private User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId))
        );
    }

    private void setFields(Item oldItem, UpdateItemDto updateItem) {
        if (updateItem.getName() != null && !updateItem.getName().isBlank()) {
            oldItem.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null && !updateItem.getDescription().isBlank()) {
            oldItem.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            oldItem.setAvailable(updateItem.getAvailable());
        }
    }
}
