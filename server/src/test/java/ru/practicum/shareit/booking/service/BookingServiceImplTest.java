package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final Long ownerId = 1L;
    private final Long bookerId = 2L;
    private final Long itemId = 1L;
    private final Long bookingId = 1L;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private CreateBookingDto newBooking;

    @BeforeEach
    void setUp() {
        owner = new User(
                ownerId,
                "owner@mail.com",
                "ownerName"
        );

        booker = new User(
                bookerId,
                "booker@mail.com",
                "bookerName"
        );

        item = new Item(
                itemId,
                "test name",
                "test desc",
                true,
                owner
        );

        booking = new Booking(
                bookingId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                booker,
                item,
                BookingStatus.APPROVED
        );

        newBooking = new CreateBookingDto(
                bookingId,
                booking.getStart(),
                booking.getEnd()
        );
    }

    @Test
    void create_WhenValidData_ShouldCreateBookingSuccessfully() {
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(ArgumentMatchers.any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.create(newBooking, bookerId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingId));
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getEnd(), equalTo(booking.getEnd()));
        assertThat(result.getItem().getId(), equalTo(itemId));
        assertThat(result.getBooker().getId(), equalTo(bookerId));

        verify(userRepository, times(1)).findById(bookerId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, times(1)).save(ArgumentMatchers.any(Booking.class));
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowNotFoundException() {
        String expectedMessage = String.format("Пользователь с id=%d не найден", bookerId);
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(newBooking, bookerId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(userRepository, times(1)).findById(bookerId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_WhenItemNotFound_ShouldThrowNotFoundException() {
        String expectedMessage = String.format("Предмет с id=%d не найден", itemId);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(newBooking, bookerId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_WhenItemNotAvailable_ShouldThrowUnavailableItemException() {
        String expectedMessage = String.format("Предмет с id=%d недоступен", itemId);
        item.setAvailable(false);
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        UnavailableItemException exception = assertThrows(UnavailableItemException.class,
                () -> bookingService.create(newBooking, bookerId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_WhenStartDateIsAfterEndDate_ShouldThrowException() {
        String expectedMessage = "Время начала бронирования должно быть до времени конца";
        CreateBookingDto invalidBooking = new CreateBookingDto(
                bookingId,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.create(invalidBooking, bookerId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(userRepository, times(1)).findById(bookerId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_WhenStartDateEqualsToEndDate_ShouldThrowException() {
        String expectedMessage = "Время начала бронирования не может быть равно времени конца";
        CreateBookingDto invalidBooking = new CreateBookingDto(
                bookingId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class,
                () -> bookingService.create(invalidBooking, bookerId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(userRepository, times(1)).findById(bookerId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_WhenBookingExistsAndUserIsOwner_ShouldApproveBooking() {
        boolean isApproved = true;
        booking.setStatus(BookingStatus.WAITING);

        Booking expectedBooking = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker(),
                booking.getItem(),
                BookingStatus.APPROVED
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        BookingDto result = bookingService.approve(bookingId, isApproved, ownerId);

        assertThat(result.getId(), equalTo(bookingId));
        assertThat(result.getStatus(), equalTo(expectedBooking.getStatus()));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(expectedBooking);
    }

    @Test
    void approve_WhenBookingExistsAndUserIsOwner_ShouldRejectBooking() {
        boolean isApproved = false;
        booking.setStatus(BookingStatus.WAITING);

        Booking expectedBooking = new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker(),
                booking.getItem(),
                BookingStatus.REJECTED
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(expectedBooking)).thenReturn(expectedBooking);

        BookingDto result = bookingService.approve(bookingId, isApproved, ownerId);

        assertThat(result.getId(), equalTo(bookingId));
        assertThat(result.getStatus(), equalTo(expectedBooking.getStatus()));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(expectedBooking);
    }

    @Test
    void approve_WhenBookingNotFound_ShouldThrowNotFoundException() {
        Boolean isApproved = true;
        String expectedMessage = String.format("Бронирование с id=%d не найдено", bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.approve(bookingId, isApproved, ownerId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_WhenUserIsNotOwner_ShouldThrowAccessDeniedException() {
        Boolean isApproved = true;
        Long notOwnerId = 999L;
        String expectedMessage = String.format(
                "Пользователь с id=%d не является владельцем вещи с id=%d", notOwnerId, itemId
        );

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                bookingService.approve(bookingId, isApproved, notOwnerId)
        );
        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getById_WhenBookingExistsAndUserIsBooker_ShouldReturnBookingDto() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        BookingDto result = bookingService.getById(bookingId, bookerId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(bookerId);
    }

    @Test
    void getById_WhenBookingExistsAndUserIsOwner_ShouldReturnBookingDto() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        BookingDto result = bookingService.getById(bookingId, ownerId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(userRepository, times(1)).findById(ownerId);
    }

    @Test
    void getById_WhenBookingDoesNotExist_ShouldThrowNotFoundException() {
        String expectedMessage = String.format("Бронирование с id=%d не найдено", bookingId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, bookerId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getById_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        long unExistingId = 999L;
        String expectedMessage = String.format("Пользователь с id=%d не найден", unExistingId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(unExistingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, unExistingId));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(userRepository, times(1)).findById(unExistingId);
    }

    @Test
    void getById_WhenUserIsNotBookerOrOwner_ShouldThrowAccessDeniedException() {
        User otherUser = new User(
                10L,
                "other@mail.com",
                "otherName"
        );
        String expectedMessage = String.format("Пользователю с id=%d запрещен доступ к бронированию с id=%d",
                otherUser.getId(), bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(otherUser.getId())).thenReturn(Optional.of(otherUser));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingService.getById(bookingId, otherUser.getId()));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getAllBookingsByUserId_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long unExistingId = 999L;
        BookingState state = BookingState.ALL;
        String expectedMessage = String.format("Пользователь с id=%d не найден", unExistingId);

        when(userRepository.findById(unExistingId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByUserId(unExistingId, state));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void getAllBookingsByUserId_ValidUserAndStateAll_ReturnsAllBookings() {
        BookingState state = BookingState.ALL;
        List<Booking> expectedBookings = List.of(booking);

        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.getAllBookingsByBookerId(bookerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(bookerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(bookingId));
        assertThat(result.get(0).getItem().getId(), equalTo(itemId));
        verify(bookingRepository, times(1)).getAllBookingsByBookerId(bookerId);
    }

    @Test
    void getAllBookingsByUserId_ValidUserAndStateCurrent_ReturnsCurrentBookings() {
        BookingState state = BookingState.CURRENT;
        booking.setEnd(LocalDateTime.now().minusSeconds(1));
        LocalDateTime now = LocalDateTime.now();
        Booking currentBooking = new Booking(
                2L,
                now.minusDays(1),
                now.plusDays(1),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(currentBooking);

        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.getCurrentBookingsByBookerId(bookerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(bookerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(2L));
        verify(bookingRepository, times(1)).getCurrentBookingsByBookerId(bookerId);
    }

    @Test
    void getAllBookingsByUserId_ValidUserAndStatePast_ReturnsPastBookings() {
        BookingState state = BookingState.PAST;
        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = new Booking(
                3L,
                now.minusDays(2),
                now.minusDays(1),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(pastBooking);

        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.getPastBookingsByBookerId(bookerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(bookerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(3L));
        verify(bookingRepository, times(1)).getPastBookingsByBookerId(bookerId);
    }

    @Test
    void getAllBookingsByUserId_ValidUserAndFutureState_ReturnsFutureBookings() {
        BookingState state = BookingState.FUTURE;
        LocalDateTime now = LocalDateTime.now();
        Booking futureBooking = new Booking(
                4L,
                now.plusDays(1),
                now.plusDays(2),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(futureBooking);

        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.getFutureBookingsByBookerId(bookerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(bookerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(4L));
        verify(bookingRepository, times(1)).getFutureBookingsByBookerId(bookerId);
    }

    @Test
    void getAllBookingsByUserId_ValidUserAndWaitingState_ReturnsWaitingBookings() {
        BookingState state = BookingState.WAITING;
        LocalDateTime now = LocalDateTime.now();
        Booking futureBooking = new Booking(
                4L,
                now.plusDays(1),
                now.plusDays(2),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(futureBooking);

        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.getWaitingBookingsByBookerId(bookerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(bookerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(4L));
        verify(bookingRepository, times(1)).getWaitingBookingsByBookerId(bookerId);
    }

    @Test
    void getAllBookingsByUserId_ValidUserAndRejectedState_ReturnsRejectedBookings() {
        BookingState state = BookingState.REJECTED;
        LocalDateTime now = LocalDateTime.now();
        Booking futureBooking = new Booking(
                4L,
                now.plusDays(1),
                now.plusDays(2),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(futureBooking);

        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.getRejectedBookingsByBookerId(bookerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(bookerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(4L));
        verify(bookingRepository, times(1)).getRejectedBookingsByBookerId(bookerId);
    }

    @Test
    void getAllBookingsByUserId_ValidUserNoBookings_ReturnsEmptyList() {
        BookingState state = BookingState.ALL;

        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.getAllBookingsByBookerId(bookerId))
                .thenReturn(List.of());

        List<BookingDto> result = bookingService.getAllBookingsByUserId(bookerId, state);

        assertThat(result, is(empty()));
        verify(bookingRepository, times(1)).getAllBookingsByBookerId(bookerId);
    }

    @Test
    void getAllBookingsByUserId_ValidUserAndNullBookings_ThrowsIllegalArgumentException() {
        BookingState state = null;
        String expectedMessage = "Не найден обработчик для state=" + state;

        when(userRepository.findById(bookerId))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.getAllBookingsByBookerId(bookerId))
                .thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByUserId(bookerId, state));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(bookingRepository, never()).getAllBookingsByBookerId(bookerId);
    }

    @Test
    void getAllBookingsByOwnerId_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long unExistingId = 999L;
        BookingState state = BookingState.ALL;
        String expectedMessage = String.format("Пользователь с id=%d не найден", unExistingId);

        when(userRepository.findById(unExistingId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByUserId(unExistingId, state));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
    }

    @Test
    void getAllBookingsByOwnerId_ValidUserAndStateAll_ReturnsAllBookings() {
        BookingState state = BookingState.ALL;
        List<Booking> expectedBookings = List.of(booking);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getAllBookingsByOwnerId(ownerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(ownerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(bookingId));
        assertThat(result.get(0).getItem().getId(), equalTo(itemId));
        verify(bookingRepository, times(1)).getAllBookingsByOwnerId(ownerId);
    }

    @Test
    void getAllBookingsByOwnerId_ValidUserAndStateCurrent_ReturnsCurrentBookings() {
        BookingState state = BookingState.CURRENT;
        booking.setEnd(LocalDateTime.now().minusSeconds(1));
        LocalDateTime now = LocalDateTime.now();
        Booking currentBooking = new Booking(
                2L,
                now.minusDays(1),
                now.plusDays(1),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(currentBooking);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getCurrentBookingsByOwnerId(ownerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(ownerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(2L));
        verify(bookingRepository, times(1)).getCurrentBookingsByOwnerId(ownerId);
    }

    @Test
    void getAllBookingsByOwnerId_ValidUserAndStatePast_ReturnsPastBookings() {
        BookingState state = BookingState.PAST;
        LocalDateTime now = LocalDateTime.now();
        Booking pastBooking = new Booking(
                3L,
                now.minusDays(2),
                now.minusDays(1),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(pastBooking);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getPastBookingsByOwnerId(ownerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(ownerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(3L));
        verify(bookingRepository, times(1)).getPastBookingsByOwnerId(ownerId);
    }

    @Test
    void getAllBookingsByOwnerId_ValidUserAndFutureState_ReturnsFutureBookings() {
        BookingState state = BookingState.FUTURE;
        LocalDateTime now = LocalDateTime.now();
        Booking futureBooking = new Booking(
                4L,
                now.plusDays(1),
                now.plusDays(2),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(futureBooking);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getFutureBookingsByOwnerId(ownerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(ownerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(4L));
        verify(bookingRepository, times(1)).getFutureBookingsByOwnerId(ownerId);
    }

    @Test
    void getAllBookingsByOwnerId_ValidUserAndWaitingState_ReturnsWaitingBookings() {
        BookingState state = BookingState.WAITING;
        LocalDateTime now = LocalDateTime.now();
        Booking futureBooking = new Booking(
                4L,
                now.plusDays(1),
                now.plusDays(2),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(futureBooking);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getWaitingBookingsByOwnerId(ownerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(ownerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(4L));
        verify(bookingRepository, times(1)).getWaitingBookingsByOwnerId(ownerId);
    }

    @Test
    void getAllBookingsByOwnerId_ValidUserAndRejectedState_ReturnsRejectedBookings() {
        BookingState state = BookingState.REJECTED;
        LocalDateTime now = LocalDateTime.now();
        Booking futureBooking = new Booking(
                4L,
                now.plusDays(1),
                now.plusDays(2),
                booker,
                item,
                BookingStatus.APPROVED
        );
        List<Booking> expectedBookings = List.of(futureBooking);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getRejectedBookingsByOwnerId(ownerId))
                .thenReturn(expectedBookings);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(ownerId, state);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(4L));
        verify(bookingRepository, times(1)).getRejectedBookingsByOwnerId(ownerId);
    }

    @Test
    void getAllBookingsByOwnerId_ValidUserNoBookings_ReturnsEmptyList() {
        BookingState state = BookingState.ALL;

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getAllBookingsByOwnerId(ownerId))
                .thenReturn(List.of());

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(ownerId, state);

        assertThat(result, is(empty()));
        verify(bookingRepository, times(1)).getAllBookingsByOwnerId(ownerId);
    }

    @Test
    void getAllBookingsByOwnerId_ValidUserAndNullBookings_ThrowsIllegalArgumentException() {
        BookingState state = null;
        String expectedMessage = "Не найден обработчик для state=" + state;

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.getAllBookingsByOwnerId(ownerId))
                .thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByOwnerId(ownerId, state));

        assertThat(exception.getMessage(), equalTo(expectedMessage));
        verify(bookingRepository, never()).getAllBookingsByOwnerId(ownerId);
    }
}