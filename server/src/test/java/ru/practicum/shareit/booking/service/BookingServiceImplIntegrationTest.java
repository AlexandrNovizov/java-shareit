package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private User booker;
    private User owner;
    private Item item;
    private CreateBookingDto createBookingDto;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.com");
        booker = userRepository.save(booker);

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.com");
        owner = userRepository.save(owner);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        createBookingDto = new CreateBookingDto();
        createBookingDto.setItemId(item.getId());
        createBookingDto.setStart(LocalDateTime.now().plusDays(1));
        createBookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_ShouldCreateBooking_WhenValidDataProvided() {
        BookingDto result = bookingService.create(createBookingDto, booker.getId());

        assertThat(result.getId(), is(notNullValue()));
        assertThat(result.getStatus(), is(BookingStatus.WAITING));
        assertThat(result.getItem().getId(), is(item.getId()));
        assertThat(result.getBooker().getId(), is(booker.getId()));
    }

    @Test
    void approve_ShouldApproveBooking_WhenOwnerApproves() {
        Booking createdBooking = new Booking();
        createdBooking.setItem(item);
        createdBooking.setBooker(booker);
        createdBooking.setStart(createBookingDto.getStart());
        createdBooking.setEnd(createBookingDto.getEnd());
        createdBooking.setStatus(BookingStatus.WAITING);
        createdBooking = bookingRepository.save(createdBooking);

        BookingDto result = bookingService.approve(createdBooking.getId(), true, owner.getId());

        assertThat(result.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void approve_ShouldRejectBooking_WhenOwnerRejects() {
        Booking createdBooking = new Booking();
        createdBooking.setItem(item);
        createdBooking.setBooker(booker);
        createdBooking.setStart(createBookingDto.getStart());
        createdBooking.setEnd(createBookingDto.getEnd());
        createdBooking.setStatus(BookingStatus.WAITING);
        createdBooking = bookingRepository.save(createdBooking);

        BookingDto result = bookingService.approve(createdBooking.getId(), false, owner.getId());

        assertThat(result.getStatus(), is(BookingStatus.REJECTED));
    }

    @Test
    void getById_ShouldReturnBooking_WhenUserIsBookerOrOwner() {
        Booking createdBooking = new Booking();
        createdBooking.setItem(item);
        createdBooking.setBooker(booker);
        createdBooking.setStart(createBookingDto.getStart());
        createdBooking.setEnd(createBookingDto.getEnd());
        createdBooking.setStatus(BookingStatus.WAITING);
        createdBooking = bookingRepository.save(createdBooking);

        BookingDto result1 = bookingService.getById(createdBooking.getId(), booker.getId());
        BookingDto result2 = bookingService.getById(createdBooking.getId(), owner.getId());

        assertThat(result1.getId(), is(createdBooking.getId()));
        assertThat(result2.getId(), is(createdBooking.getId()));
    }

    @Test
    void getAllBookingsByUserId_ShouldReturnBookings_WhenStateIsAll() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(booker.getId(), BookingState.ALL);

        assertThat(result, hasSize(5));
        assertThat(result.stream().map(BookingDto::getStatus).collect(Collectors.toList()),
                hasItems(BookingStatus.REJECTED, BookingStatus.WAITING, BookingStatus.APPROVED));
    }

    @Test
    void getAllBookingsByOwnerId_ShouldReturnBookings_WhenStateIsCurrent() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(owner.getId(), BookingState.CURRENT);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void getAllBookingsByUserId_ShouldReturnBookings_WhenStateIsPast() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(booker.getId(), BookingState.PAST);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatus(), is(BookingStatus.APPROVED));
        assertThat(result.get(0).getItem().getName(), is("Item1"));
    }

    @Test
    void getAllBookingsByUserId_ShouldReturnBookings_WhenStateIsFuture() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(booker.getId(), BookingState.FUTURE);

        assertThat(result, hasSize(1));
        assertThat(result.stream().map(BookingDto::getStatus).collect(Collectors.toList()),
                hasItems(BookingStatus.APPROVED));
    }

    @Test
    void getAllBookingsByUserId_ShouldReturnBookings_WhenStateIsWaiting() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(booker.getId(), BookingState.WAITING);

        assertThat(result, hasSize(1));
        assertThat(result.stream().allMatch(b -> b.getStatus() == BookingStatus.WAITING), is(true));
    }

    @Test
    void getAllBookingsByUserId_ShouldReturnBookings_WhenStateIsRejected() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(booker.getId(), BookingState.REJECTED);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatus(), is(BookingStatus.REJECTED));
    }

    @Test
    void getAllBookingsByOwnerId_ShouldReturnBookings_WhenStateIsPast() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(owner.getId(), BookingState.PAST);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void getAllBookingsByOwnerId_ShouldReturnBookings_WhenStateIsFuture() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(owner.getId(), BookingState.FUTURE);

        assertThat(result, hasSize(1));
        assertThat(result.stream().map(BookingDto::getStatus).collect(Collectors.toList()),
                hasItems(BookingStatus.APPROVED));
    }

    @Test
    void getAllBookingsByOwnerId_ShouldReturnBookings_WhenStateIsWaiting() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(owner.getId(), BookingState.WAITING);

        assertThat(result, hasSize(1));
        assertThat(result.stream().allMatch(b -> b.getStatus() == BookingStatus.WAITING), is(true));
    }

    @Test
    void getAllBookingsByOwnerId_ShouldReturnBookings_WhenStateIsRejected() {
        createSampleBookings(booker, owner);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(owner.getId(), BookingState.REJECTED);

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getStatus(), is(BookingStatus.REJECTED));
    }

    private void createSampleBookings(User booker, User owner) {
        Item item1 = new Item(null, "Item1", "Desc1", true, owner);
        item1 = itemRepository.save(item1);

        LocalDateTime now = LocalDateTime.now();

        // Прошлое бронирование (PAST)
        Booking pastBooking = new Booking();
        pastBooking.setItem(item1);
        pastBooking.setBooker(booker);
        pastBooking.setStart(now.minusDays(3));
        pastBooking.setEnd(now.minusDays(2));
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        // Текущее бронирование (CURRENT)
        Booking currentBooking = new Booking();
        currentBooking.setItem(item1);
        currentBooking.setBooker(booker);
        currentBooking.setStart(now.minusDays(1));
        currentBooking.setEnd(now.plusDays(1));
        currentBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(currentBooking);

        // Будущее бронирование (FUTURE)
        Booking futureBooking = new Booking();
        futureBooking.setItem(item1);
        futureBooking.setBooker(booker);
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(2));
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        // Ожидание (WAITING)
        Booking waitingBooking = new Booking();
        waitingBooking.setItem(item1);
        waitingBooking.setBooker(booker);
        waitingBooking.setStart(now.plusDays(3));
        waitingBooking.setEnd(now.plusDays(4));
        waitingBooking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(waitingBooking);

        // Отклонённое (REJECTED)
        Booking rejectedBooking = new Booking();
        rejectedBooking.setItem(item1);
        rejectedBooking.setBooker(booker);
        rejectedBooking.setStart(now.plusDays(5));
        rejectedBooking.setEnd(now.plusDays(6));
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(rejectedBooking);
    }
}
