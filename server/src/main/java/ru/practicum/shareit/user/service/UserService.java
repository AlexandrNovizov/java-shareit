package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto create(CreateUserDto newUser);

    UserDto update(UpdateUserDto updateUserDto, Long userId);

    void delete(Long id);

    UserDto getById(Long id);

}
