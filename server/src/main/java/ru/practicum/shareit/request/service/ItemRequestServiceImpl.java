package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
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

import java.util.List;
import java.util.Objects;

import static ru.practicum.shareit.common.EntityUtils.findOrThrow;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, CreateItemRequestDto dto) {
        User user = findOrThrow(userRepository, userId);

        ItemRequest newRequest = ItemRequestDtoMapper.mapToEntity(dto, user);

        ItemRequest savedRequest = itemRequestRepository.save(newRequest);

        return ItemRequestDtoMapper.mapToDto(savedRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllByOwnerId(Long ownerId) {
        findOrThrow(userRepository, ownerId);

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

        ItemRequest request = findOrThrow(itemRequestRepository, requestId);

        return ItemRequestWithItemsDtoMapper.mapToDto(request);
    }

    @Override
    public ItemRequestWithItemsDto addItem(Long userId, Long requestId, Long itemId) {
        findOrThrow(userRepository, userId);

        ItemRequest request = findOrThrow(itemRequestRepository, requestId);

        Item item = findOrThrow(itemRepository, itemId);

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new AccessDeniedException(String.format(
                    "Пользователь с id=%d не является владельцем вещи с id=%d", userId, item.getId()
            ));
        }

        if (!request.getItems().contains(item)) {
            request.getItems().add(item);
            itemRequestRepository.save(request);
        }

        return ItemRequestWithItemsDtoMapper.mapToDto(request);
    }
}
