package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemRequestWithItemsDto {
    private Long id;
    private String description;
    private Long userId;
    private LocalDateTime created;
    private List<RequestItemInfoDto> items = new ArrayList<>();
}
