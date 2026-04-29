package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final List<Item> items;
    private long nextId = -1;

    @Override
    public Item create(Item newItem) {
        newItem.setId(getNextId());
        items.add(newItem);
        return newItem;
    }

    @Override
    public Item update(Item itemToUpdate) {
        Item oldItem = findById(itemToUpdate.getId()).get();

        Item updated = setFields(oldItem, itemToUpdate);

        return updated;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny();
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .toList();
    }

    @Override
    public List<Item> search(String query) {
        return items.stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(query) ||
                        item.getDescription().toLowerCase().contains(query)
                )
                .toList();
    }

    private long getNextId() {
        if (nextId == -1) {
            nextId = items.stream()
                    .mapToLong(Item::getId)
                    .max()
                    .orElse(0L);
        }

        return ++nextId;
    }

    private Item setFields(Item oldItem, Item newItem) {
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        return oldItem;
    }
}
