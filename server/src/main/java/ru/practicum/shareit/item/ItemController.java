package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemDto getById(
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        return itemService.getById(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestBody @Validated CreateItemDto newItem,
            @RequestHeader(USER_ID_HEADER) Long ownerId
    ) {

        return itemService.create(newItem, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable Long itemId,
            @RequestBody UpdateItemDto updateItem,
            @RequestHeader(USER_ID_HEADER) Long ownerId
    ) {

        return itemService.update(updateItem, itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.getAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByQuery(@RequestParam("text") String query) {
        return itemService.search(query);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @PathVariable Long itemId,
            @RequestBody @Validated CreateCommentDto createDto,
            @RequestHeader(USER_ID_HEADER) Long ownerId) {

        return itemService.addComment(createDto, itemId, ownerId);
    }
}
