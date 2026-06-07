package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemServiceImpl itemService;

    private User owner;
    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private CreateItemDto createItemDto;
    private UpdateItemDto updateItemDto;
    private CreateCommentDto createCommentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        owner = new User(
                null,
                "owner@test.com",
                "Owner"
        );
        owner = userRepository.save(owner);

        user = new User(
                null,
                "other@test.com",
                "Other User"
        );
        user = userRepository.save(user);

        item = new Item(
                null,
                "Test Item",
                "Test description",
                true,
                owner
        );
        item = itemRepository.save(item);

        itemRequest = new ItemRequest(
                null,
                "Test request",
                owner,
                LocalDateTime.now(),
                new HashSet<>()
        );
        itemRequest = itemRequestRepository.save(itemRequest);

        createItemDto = new CreateItemDto(
                "New Item",
                "New Item Test description",
                true,
                itemRequest.getId()
        );

        updateItemDto = new UpdateItemDto(
                "Updated Item",
                "Updated description",
                false
        );

        comment = new Comment(
                null,
                "Great item!",
                LocalDateTime.now(),
                user,
                item
        );
        comment = commentRepository.save(comment);

        createCommentDto = new CreateCommentDto(comment.getText());
    }

    @Test
    void createItem_ShouldCreateItemSuccessfully() {
        ItemDto createdItem = itemService.create(createItemDto, owner.getId());

        assertThat(createdItem.getName(), equalTo(createItemDto.getName()));
        assertThat(createdItem.getDescription(), equalTo(createItemDto.getDescription()));
        assertThat(createdItem.getAvailable(), equalTo(createItemDto.getAvailable()));
    }

    @Test
    void updateItem_ShouldUpdateItemSuccessfully() {
        ItemDto updatedItem = itemService.update(updateItemDto, item.getId(), owner.getId());

        assertThat(updatedItem.getName(), equalTo(updateItemDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemDto.getAvailable()));
    }

    @Test
    void getItemById_ShouldReturnItemWithBookingInfoAndComments() {
        ItemDto itemDto = itemService.getById(item.getId(), owner.getId());

        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getComments(), hasSize(1));
        assertThat(itemDto.getComments().get(0).getText(), equalTo(comment.getText()));
    }

    @Test
    void getAllItemsByOwnerId_ShouldReturnAllItemsWithBookingInfoAndComments() {
        Item otherItem = new Item(
                null,
                "Second Item",
                "Second Desc",
                true,
                owner
        );
        otherItem = itemRepository.save(otherItem);

        Comment otherComment = new Comment(
                null,
                "Another comment",
                LocalDateTime.now(),
                user,
                otherItem
        );
        otherComment = commentRepository.save(otherComment);

        List<ItemDto> items = itemService.getAllByOwnerId(owner.getId());

        assertThat(items, hasSize(2));
        assertThat(items, hasItems(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("id", equalTo(otherItem.getId()))
        ));

        assertThat(items.get(0).getComments(), hasSize(1));
        assertThat(items.get(1).getComments(), hasSize(1));
    }

    @Test
    void searchItems_ShouldReturnItemsMatchingQuery() {
        Item otherItem = new Item(
                null,
                "Another Test Item",
                "Different description",
                true,
                owner
        );
        otherItem = itemRepository.save(otherItem);

        List<ItemDto> searchResults = itemService.search("Test");

        assertThat(searchResults, hasSize(2));
        assertThat(searchResults, hasItems(
                hasProperty("name", containsString("Test")),
                hasProperty("description", containsString("Test"))
        ));
    }

    @Test
    void addComment_ShouldAddCommentSuccessfully() {
        Booking booking = new Booking(
                null,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(5),
                user,
                item,
                BookingStatus.APPROVED
        );
        booking = bookingRepository.save(booking);

        List<Comment> comments = commentRepository.findCommentsByItemId(item.getId());
        assertThat(comments, hasSize(1));
        assertThat(comments.get(0).getText(), equalTo(createCommentDto.getText()));
    }
}
