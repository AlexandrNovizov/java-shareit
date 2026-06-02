package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestBody @Valid CreateItemRequestDto dto) {

        return itemRequestService.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {

        return itemRequestService.getAllByOwnerId(ownerId);
    }
}
