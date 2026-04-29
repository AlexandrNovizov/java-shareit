package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(CreateUserDto newUser) {

        if (!userRepository.isUniqueEmail(newUser.getEmail())) {
            throw new EmailAlreadyExistsException("Пользователь с таким email уже существует");
        }

        User user = userRepository.create(UserDtoMapper.mapToUser(newUser));

        return UserDtoMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(UpdateUserDto updateDto, Long userId) {

        UserDto userToUpdate = getById(userId);

        throwExceptionIfNotUniqueEmail(updateDto.getEmail());
        userToUpdate.setEmail(updateDto.getEmail());
        userToUpdate.setName(updateDto.getName());

        User receivedUser = userRepository.update(UserDtoMapper.mapToUser(userToUpdate));

        return UserDtoMapper.mapToUserDto(receivedUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public UserDto getById(Long id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", id))
        );

        return UserDtoMapper.mapToUserDto(user);
    }

    private void throwExceptionIfNotUniqueEmail(String email) {
        if (!userRepository.isUniqueEmail(email)) {
            throw new EmailAlreadyExistsException("Пользователь с таким email уже существует");
        }
    }
}
