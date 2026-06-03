package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class RequestItemInfoDto {

    private Long itemId;

    private String name;

    private Long ownerId;
}
