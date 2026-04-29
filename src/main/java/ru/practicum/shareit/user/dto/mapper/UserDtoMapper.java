package ru.practicum.shareit.user.dto.mapper;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserDtoMapper {

    public static User mapToUser(UserDto dto) {
        User user = new User();

        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        return user;
    }
    
    public static User mapToUser(CreateUserDto dto) {
        User user = new User();
        
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        
        return user;
    }

    public static UserDto mapToUserDto(User entity) {
        UserDto dto = new UserDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());

        return dto;
    }
}
