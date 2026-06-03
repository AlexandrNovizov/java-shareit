package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
