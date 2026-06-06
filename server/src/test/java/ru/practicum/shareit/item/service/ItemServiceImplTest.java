package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.projection.LastAndNextBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
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
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    private CreateItemDto newItem;
    private User user;
    private Item item;
    private ItemRequest request;
    private UpdateItemDto updateItem;
    private final Long userId = 1L;
    private final Long requestId = 1L;
    private final Long itemId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        newItem = new CreateItemDto(
                "Test Item",
                "Test Description",
                true,
                null
        );

        item = new Item(
                itemId,
                newItem.getName(),
                newItem.getDescription(),
                newItem.getAvailable(),
                user
        );

        request = new ItemRequest(
            requestId,
            "test desc",
            user,
            LocalDateTime.now(),
            new HashSet<>()
        );

        updateItem = new UpdateItemDto(
                "Updated Name",
                "Updated Desc",
                false
        );
    }

    @Test
    void create_WithValidData_ShouldReturnItemDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(newItem, userId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).save(ArgumentMatchers.any(Item.class));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void create_WithRequestId_ShouldReturnItemDto() {
        newItem.setRequestId(requestId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(newItem, userId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).save(ArgumentMatchers.any(Item.class));
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void create_WhenOwnerNotFound_ShouldThrowNotFoundException() {
        String expectedMessage = String.format("Пользователь с id=%d не найден", userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemService.create(newItem, userId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).findById(requestId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void create_WhenRequestNotFound_ShouldThrowNotFoundException() {
        String expectedMessage = String.format("Запрос с id=%d не найден", requestId);
        newItem.setRequestId(requestId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemId)).thenReturn(Optional.empty());
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenReturn(item);

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemService.create(newItem, userId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void update_WithValidData_ShouldUpdateAndReturnItemDto() {
        Item updatedItem = new Item(
                item.getId(),
                updateItem.getName(),
                updateItem.getDescription(),
                updateItem.getAvailable(),
                user
        );
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.update(updateItem, itemId, userId);

        assertThat(result.getId(), equalTo(itemId));
        assertThat(result.getName(), equalTo(updateItem.getName()));
        assertThat(result.getDescription() ,equalTo(updateItem.getDescription()));
        assertThat(result.getAvailable(), equalTo(updateItem.getAvailable()));
        verify(itemRepository, times(1)).save(ArgumentMatchers.any(Item.class));
    }

    @Test
    void update_WithPartialUpdate_ShouldUpdateOnlySpecifiedFields() {
        updateItem.setAvailable(null);
        updateItem.setDescription(null);
        Item updatedItem = new Item(
                item.getId(),
                updateItem.getName(),
                item.getDescription(),
                item.getAvailable(),
                user
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.save(ArgumentMatchers.any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.update(updateItem, itemId, userId);

        assertThat(result.getId(), equalTo(itemId));
        assertThat(result.getName(), equalTo(updateItem.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void update_WhenUserDoesNotExist_ShouldThrowException() {
        long unExistingId = 999L;

        when(userRepository.findById(unExistingId))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));


        assertThrows(NotFoundException.class,
                () -> itemService.update(updateItem, itemId, unExistingId));

        verify(userRepository, times(1)).findById(unExistingId);
    }

    @Test
    void update_WhenItemDoesNotExist_ShouldThrowException() {
        Long unExistingId = 999L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(unExistingId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(updateItem, unExistingId, userId));
        verify(itemRepository, times(1)).findById(unExistingId);
    }

    @Test
    void update_WhenOwnerDoesNotMatch_ShouldThrowAccessDeniedException() {
        User otherUser = new User(
                100L,
                "other@mail.ru",
                "Other name"
        );
        String expectedMessage = String.format("Пользователь с id=%d не является владельцем вещи с id=%d",
                otherUser.getId(), itemId);

        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));

        Throwable exception = assertThrows(AccessDeniedException.class,
                () -> itemService.update(updateItem, itemId, otherUser.getId()));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(itemRepository, never()).save(ArgumentMatchers.any(Item.class));
    }

    @Test
    void getById_WhenItemExistsAndUserIsOwner_ShouldReturnItemWithBookingInfoAndComments() {
        List<LastAndNextBooking> bookingInfos = List.of(
                new LastAndNextBooking(
                    itemId,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1)
        ));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.getLastAndNextBookingForIds(any())).thenReturn(bookingInfos);
        when(commentRepository.findCommentsByItemId(itemId)).thenReturn(List.of());

        ItemDto result = itemService.getById(itemId, userId);

        assertThat(result.getId(), equalTo(itemId));
        assertThat(result.getLastBooking(), equalTo(bookingInfos.get(0).getLast()));
        assertThat(result.getNextBooking(), equalTo(bookingInfos.get(0).getNext()));
        assertThat(result.getComments(), empty());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).getLastAndNextBookingForIds(List.of(itemId));
        verify(commentRepository, times(1)).findCommentsByItemId(itemId);
    }

    @Test
    void getById_WhenItemExistsButUserIsNotOwner_ShouldReturnItemWithoutBookingInfo() {
        User otherUser = new User(
                100L,
                "other@mail.ru",
                "otherName"
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(otherUser));
        when(commentRepository.findCommentsByItemId(itemId)).thenReturn(List.of());

        ItemDto result = itemService.getById(itemId, otherUser.getId());

        assertThat(result.getId(), equalTo(itemId));
        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), nullValue());
        verify(bookingRepository, never()).getLastAndNextBookingForIds(any());
    }

    @Test
    void getById_WhenItemDoesNotExist_ShouldThrowException() {
        String exceptionMessage = String.format("Предмет с id=%d не найден", itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(itemId, userId));

        assertThat(exception.getMessage(), equalTo(exceptionMessage));
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).getLastAndNextBookingForIds(any());
        verify(commentRepository, never()).findCommentsByItemId(anyLong());
    }

    @Test
    void getById_WhenUserDoesNotExist_ShouldThrowException() {
        String expectedMessage = String.format("Пользователь с id=%d не найден", userId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(itemId, userId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).getLastAndNextBookingForIds(any());
        verify(commentRepository, never()).findCommentsByItemId(anyLong());
    }

    @Test
    void getById_WhenItemHasComments_ShouldReturnItemWithCommentsList() {
        List<Comment> comments = List.of(
                new Comment(
                        1L,
                        "text",
                        LocalDateTime.now(),
                        user,
                        item
                )
        );

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findCommentsByItemId(itemId)).thenReturn(comments);

        ItemDto result = itemService.getById(itemId, userId);

        assertThat(result.getId(), equalTo(itemId));
        assertThat(result.getComments(), hasSize(1));

        Comment comment = comments.get(0);
        CommentDto dto = result.getComments().get(0);

        assertThat(dto.getId(), equalTo(comment.getId()));
        assertThat(dto.getText(), equalTo(comment.getText()));
        assertThat(dto.getCreated(), equalTo(comment.getCreated()));
        assertThat(dto.getAuthorName(), equalTo(comment.getUser().getName()));
        verify(itemRepository, times(1)).findById(itemId);
        verify(commentRepository, times(1)).findCommentsByItemId(itemId);
    }

    @Test
    void getById_WhenOwnerHasBookingInfoButNoBookings_ShouldReturnItemWithNullBookingInfo() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.getLastAndNextBookingForIds(List.of(itemId))).thenReturn(List.of());
        when(commentRepository.findCommentsByItemId(itemId)).thenReturn(List.of());

        ItemDto result = itemService.getById(itemId, userId);

        assertThat(result.getId(), equalTo(itemId));
        assertThat(result.getNextBooking(), nullValue());
        assertThat(result.getLastBooking(), nullValue());
        verify(bookingRepository, times(1)).getLastAndNextBookingForIds(List.of(itemId));
    }

    @Test
    void getAllByOwnerId_WhenItemsExist_ShouldReturnItemsWithBookingAndComments() {
        LocalDateTime last = LocalDateTime.of(2000, 2, 21, 0, 0);
        LastAndNextBooking booking = new LastAndNextBooking(itemId, last, last.plusYears(10));
        Comment comment = new Comment(1L, "text", LocalDateTime.now(), user, item);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));
        when(bookingRepository.getLastAndNextBookingForIds(List.of(itemId))).thenReturn(List.of(booking));
        when(commentRepository.findCommentsByItemOwnerId(userId)).thenReturn(List.of(comment));

        List<ItemDto> result = itemService.getAllByOwnerId(userId);

        // Then
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getComments(), hasSize(1));
        assertThat(result.get(0).getComments().get(0).getText(), equalTo(comment.getText()));
        assertThat(result.get(0).getLastBooking(), equalTo(booking.getLast()));
        assertThat(result.get(0).getNextBooking(), equalTo(booking.getNext()));
        verify(itemRepository, times(1)).findByOwnerId(userId);
        verify(bookingRepository, times(1)).getLastAndNextBookingForIds(List.of(itemId));
        verify(commentRepository, times(1)).findCommentsByItemOwnerId(userId);
    }

    @Test
    void getAllByOwnerId_WhenNoItems_ShouldReturnEmptyList() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllByOwnerId(userId);

        assertThat(result, is(empty()));
        verify(itemRepository, times(1)).findByOwnerId(userId);
        verify(bookingRepository, never()).getLastAndNextBookingForIds(any());
        verify(commentRepository, never()).findCommentsByItemOwnerId(any());
    }

    @Test
    void getAllByOwnerId_WhenUserNotFound_ShouldThrowException() {
        String expectedMessage = String.format("Пользователь с id=%d не найден", userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemService.getAllByOwnerId(userId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).findByOwnerId(any());
    }

    @Test
    void getAllByOwnerId_WhenItemHasNoBookings_ShouldReturnItemsWithoutBookingInfo() {
        List<Comment> comments = List.of(
                new Comment(
                        1L,
                        "text",
                        LocalDateTime.now(),
                        user,
                        item
                )
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));
        when(bookingRepository.getLastAndNextBookingForIds(List.of(itemId))).thenReturn(List.of());
        when(commentRepository.findCommentsByItemOwnerId(userId)).thenReturn(comments);

        List<ItemDto> result = itemService.getAllByOwnerId(userId);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getLastBooking(), nullValue());
        assertThat(result.get(0).getNextBooking(), nullValue());
        assertThat(result.get(0).getComments(), not(empty()));

        Comment comment = comments.get(0);
        CommentDto dto = result.get(0).getComments().get(0);

        assertThat(dto.getId(), equalTo(comment.getId()));
        assertThat(dto.getText(), equalTo(comment.getText()));
        assertThat(dto.getCreated(), equalTo(comment.getCreated()));
        assertThat(dto.getAuthorName(), equalTo(comment.getUser().getName()));
        verify(bookingRepository, times(1)).getLastAndNextBookingForIds(List.of(itemId));
    }

    @Test
    void getAllByOwnerId_WhenItemHasNoComments_ShouldReturnItemsWithoutComments() {
        LocalDateTime last = LocalDateTime.of(2000, 2, 21, 0, 0);
        LastAndNextBooking booking = new LastAndNextBooking(itemId, last, last.plusYears(10));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item));
        when(bookingRepository.getLastAndNextBookingForIds(List.of(itemId))).thenReturn(List.of(booking));
        when(commentRepository.findCommentsByItemOwnerId(userId)).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllByOwnerId(userId);

        // Then
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getComments(), is(empty()));
        assertThat(result.get(0).getLastBooking(), equalTo(booking.getLast()));
        assertThat(result.get(0).getNextBooking(), equalTo(booking.getNext()));
        verify(commentRepository, times(1)).findCommentsByItemOwnerId(userId);
    }

    @Test
    void testSearch_WithEmptyQuery_ShouldReturnEmptyList() {
        String query = "";

        List<ItemDto> result = itemService.search(query);

        assertThat(result, empty());
        verify(itemRepository, never()).searchByQuery(anyString());
    }

    @Test
    void testSearch_WithValidQuery_NoResults_ShouldReturnEmptyList() {
        String query = "nonexistent item";
        when(itemRepository.searchByQuery(query)).thenReturn(List.of());

        List<ItemDto> result = itemService.search(query);

        assertThat(result, empty());
        verify(itemRepository, times(1)).searchByQuery(query);
    }

    @Test
    void testSearch_WithValidQuery_ShouldReturnMappedDtos() {
        String query = "test";

        when(itemRepository.searchByQuery(query)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.search(query);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDescription(), equalTo(item.getDescription()));
        verify(itemRepository, times(1)).searchByQuery(query);
    }

    @Test
    void addComment_WithValidData_ShouldAddComment() {
        CreateCommentDto newComment = new CreateCommentDto("text");
        Comment expectedComment = new Comment(1L, newComment.getText(), LocalDateTime.now(), user, item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.hasItemBookedByUser(itemId, userId)).thenReturn(true);
        when(commentRepository.save(ArgumentMatchers.any(Comment.class))).thenReturn(expectedComment);

        CommentDto result = itemService.addComment(newComment, itemId, userId);

        assertThat(result.getId(), equalTo(expectedComment.getId()));
        assertThat(result.getText(), equalTo(newComment.getText()));
        assertThat(result.getAuthorName(), equalTo(user.getName()));
        assertThat(result.getCreated(), equalTo(expectedComment.getCreated()));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).hasItemBookedByUser(itemId, userId);
        verify(commentRepository, times(1)).save(ArgumentMatchers.any(Comment.class));
    }

    @Test
    void addComment_UserNotFound_ThrowsException() {
        String expectedMessage = String.format("Пользователь с id=%d не найден", userId);
        CreateCommentDto newComment = new CreateCommentDto("text");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(newComment, itemId, userId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, never()).hasItemBookedByUser(any(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_ItemNotFound_ThrowsException() {
        String expectedMessage = String.format("Предмет с id=%d не найден", itemId);
        CreateCommentDto newComment = new CreateCommentDto("text");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.empty());

        Throwable exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(newComment, itemId, userId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).hasItemBookedByUser(any(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_ItemNotBookedByUser_ThrowsException() {
        String expectedMessage = String.format("Предмет с id=%d не был бронирован пользователем с id=%d", itemId, userId);
        CreateCommentDto newComment = new CreateCommentDto("text");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));
        when(bookingRepository.hasItemBookedByUser(itemId, userId)).thenReturn(false);

        Throwable exception = assertThrows(ConditionsNotMetException.class,
                () -> itemService.addComment(newComment, itemId, userId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(bookingRepository, times(1)).hasItemBookedByUser(itemId, userId);
        verify(commentRepository, never()).save(any());
    }
}
