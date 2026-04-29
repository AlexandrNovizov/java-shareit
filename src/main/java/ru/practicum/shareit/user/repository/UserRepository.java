package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {

    User create(User newUser);

    User update(User userToUpdate);

    void delete(Long userId);

    Optional<User> findById(Long userId);

    boolean isUniqueEmail(String email);
}
