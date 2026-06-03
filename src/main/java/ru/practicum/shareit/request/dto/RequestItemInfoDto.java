package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestItemInfoDto {

    @NotNull
    private Long itemId;

    @NotBlank
    private String name;

    @NotNull
    private Long ownerId;
}
