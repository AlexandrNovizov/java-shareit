package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @PathVariable Long itemId,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        return itemClient.getItem(userId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestBody CreateItemDto newItem,
            @RequestHeader(USER_ID_HEADER) Long ownerId
    ) {

        return itemClient.createItem(ownerId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable Long itemId,
            @RequestBody UpdateItemDto updateItem,
            @RequestHeader(USER_ID_HEADER) Long ownerId
    ) {

        return itemClient.updateItem(ownerId, itemId, updateItem);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.getAllByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByQuery(@RequestParam("text") String query) {
        return itemClient.search(query);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> addComment(
            @PathVariable Long itemId,
            @RequestBody CreateCommentDto createDto,
            @RequestHeader(USER_ID_HEADER) Long ownerId) {

        return itemClient.addComment(ownerId, itemId, createDto);
    }
}
