package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.projection.LastAndNextBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.mapper.CommentDtoMapper;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.common.EntityUtils.findOrThrow;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(CreateItemDto newItem, Long ownerId) {
        User owner = findOrThrow(userRepository, ownerId);

        ItemRequest request = null;
        if (newItem.getRequestId() != null) {
            Long requestId = newItem.getRequestId();
            request = findOrThrow(itemRequestRepository, requestId);
        }

        Item createdItem = itemRepository.save(ItemDtoMapper.mapToItem(newItem, owner));

        if (request != null) {
            if (!request.getItems().contains(createdItem)) {
                request.getItems().add(createdItem);
                itemRequestRepository.save(request);
            }
        }

        return ItemDtoMapper.mapToDto(createdItem);
    }

    @Override
    public ItemDto update(UpdateItemDto updateItemDto, Long itemId, Long ownerId) {
        findOrThrow(userRepository, ownerId);

        Item oldItem = findOrThrow(itemRepository, itemId);
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
    public ItemDto getById(Long itemId, Long userId) {
        Item item = findOrThrow(itemRepository, itemId);

        findOrThrow(userRepository, userId);

        ItemDto dto = ItemDtoMapper.mapToDto(item);

        LastAndNextBooking bookingInfo = null;
        if (item.getOwner().getId().equals(userId)) {
            List<LastAndNextBooking> bookingInfos = bookingRepository.getLastAndNextBookingForIds(List.of(itemId));
            if (!bookingInfos.isEmpty()) {
                bookingInfo = bookingInfos.get(0);
            }
        }

        ItemDtoMapper.setBookingInfo(dto, bookingInfo);

        List<CommentDto> comments = commentRepository.findCommentsByItemId(itemId).stream()
                .map(CommentDtoMapper::mapToCommentDto)
                .toList();

        dto.setComments(comments);

        return dto;
    }

    @Override
    public List<ItemDto> getAllByOwnerId(Long ownerId) {
        findOrThrow(userRepository, ownerId);

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

        List<ItemDto> itemDtos = ItemDtoMapper.setBookingInfo(items, bookingInfo);

        Map<Long, List<Comment>> comments = commentRepository.findCommentsByItemOwnerId(ownerId)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        for (ItemDto itemDto : itemDtos) {
            List<CommentDto> commentDtos = comments.getOrDefault(itemDto.getId(), new ArrayList<>())
                    .stream()
                    .map(CommentDtoMapper::mapToCommentDto)
                    .toList();
            itemDto.setComments(commentDtos);
        }

        return itemDtos;
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

    @Override
    public CommentDto addComment(CreateCommentDto newComment, Long itemId, Long userId) {
        User user = findOrThrow(userRepository, userId);

        Item item = findOrThrow(itemRepository, itemId);

        if (!bookingRepository.hasItemBookedByUser(itemId, userId)) {
            throw new ConditionsNotMetException(
                    String.format("Предмет с id=%d не был бронирован пользователем с id=%d", itemId, userId)
            );
        }

        Comment createdComment = commentRepository.save(
                CommentDtoMapper.mapToComment(newComment, user, item)
        );
        return CommentDtoMapper.mapToCommentDto(createdComment);
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
