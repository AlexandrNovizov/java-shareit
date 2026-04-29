package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final List<User> users;
    private long nextId = -1;

    @Override
    public User create(User newUser) {
        newUser.setId(getNextId());
        users.add(newUser);
        return newUser;
    }

    @Override
    public User update(User userToUpdate) {
        User oldUser = findById(userToUpdate.getId()).get();

        User updated = setFields(oldUser, userToUpdate);

        return updated;
    }

    @Override
    public void delete(Long userId) {
        findById(userId).ifPresent(users::remove);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return users.stream()
                .filter(user -> Objects.equals(user.getId(), userId))
                .findAny();
    }

    @Override
    public boolean isUniqueEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .isEmpty();
    }

    private User setFields(User oldUser, User newUser) {
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        return oldUser;
    }

    private long getNextId() {
        if (nextId == -1) {
            nextId = users.stream()
                    .mapToLong(User::getId)
                    .max()
                    .orElse(0L);
        }

        return ++nextId;
    }
}
