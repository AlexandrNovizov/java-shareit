package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestWithItemsDto {
    private Long id;
    private String description;
    private Long userId;
    private LocalDateTime created;
    private List<RequestItemInfoDto> items = new ArrayList<>();
}
