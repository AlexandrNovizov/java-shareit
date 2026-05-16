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

        throwExceptionIfNotUniqueEmail(newUser.getEmail());

        User user = userRepository.save(UserDtoMapper.mapToUser(newUser));

        return UserDtoMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(UpdateUserDto updateDto, Long userId) {

        User userToUpdate = getUserOrThrowException(userId);
        throwExceptionIfNotUniqueEmail(updateDto.getEmail());
        setFields(userToUpdate, updateDto);

        User receivedUser = userRepository.save(userToUpdate);

        return UserDtoMapper.mapToUserDto(receivedUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto getById(Long id) {

        User user = getUserOrThrowException(id);

        return UserDtoMapper.mapToUserDto(user);
    }

    private void setFields(User oldUser, UpdateUserDto newUser) {
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            oldUser.setEmail(newUser.getEmail());
        }
    }

    private User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId))
        );
    }

    private void throwExceptionIfNotUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Пользователь с таким email уже существует");
        }
    }
}
