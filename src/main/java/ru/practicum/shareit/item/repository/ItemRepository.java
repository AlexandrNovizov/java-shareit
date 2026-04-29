package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item create(Item newItem);

    Item update(Item itemToUpdate);

    Optional<Item> findById(Long itemId);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> search(String query);
}
