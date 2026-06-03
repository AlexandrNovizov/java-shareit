package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestDtoMapper;
import ru.practicum.shareit.request.dto.mapper.ItemRequestWithItemsDtoMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, CreateItemRequestDto dto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId))
        );

        ItemRequest newRequest = ItemRequestDtoMapper.mapToEntity(dto, user);

        ItemRequest savedRequest = itemRequestRepository.save(newRequest);

        return ItemRequestDtoMapper.mapToDto(savedRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllByOwnerId(Long ownerId) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId))
        );

        List<ItemRequest> userRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(ownerId);

        return userRequests.stream()
                .map(ItemRequestWithItemsDtoMapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll() {

        List<ItemRequest> allRequests = itemRequestRepository.findAll(Sort.by(Sort.Order.desc("created")));

        return allRequests.stream()
                .map(ItemRequestDtoMapper::mapToDto)
                .toList();
    }

    @Override
    public ItemRequestWithItemsDto getById(Long requestId) {

        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос с id=%d не найден", requestId))
        );

        return ItemRequestWithItemsDtoMapper.mapToDto(request);
    }

    @Override
    public ItemRequestWithItemsDto addItem(Long requestId, Long itemId) {
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Запрос с id=%d не найден", requestId))
        );

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Предмет с id=%d не найден", itemId))
        );

        if (!request.getItems().contains(item)) {
            request.getItems().add(item);
            itemRequestRepository.save(request);
        }

        return ItemRequestWithItemsDtoMapper.mapToDto(request);
    }
}
