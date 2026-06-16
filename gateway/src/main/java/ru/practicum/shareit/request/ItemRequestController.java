package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @RequestBody CreateItemRequestDto dto) {

        return itemRequestClient.createRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByOwner(@RequestHeader(USER_ID_HEADER) Long ownerId) {

        return itemRequestClient.getAllByOwnerId(ownerId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        return itemRequestClient.getAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId) {
        return itemRequestClient.getRequest(requestId);
    }

    @PatchMapping("/{requestId}/add/{itemId}")
    public ResponseEntity<Object> addItemToRequest(@PathVariable Long requestId,
                                                   @PathVariable Long itemId,
                                                   @RequestHeader(USER_ID_HEADER) Long userId) {

        return itemRequestClient.addItem(userId, requestId, itemId);
    }
}
