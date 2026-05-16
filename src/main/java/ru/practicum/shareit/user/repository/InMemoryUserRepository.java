package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User newUser) {
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User update(User userToUpdate) {
        return users.replace(userToUpdate.getId(), userToUpdate);
    }

    @Override
    public void delete(Long userId) {
        findById(userId).ifPresent(ignored -> users.remove(userId));
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public boolean isUniqueEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .isEmpty();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
