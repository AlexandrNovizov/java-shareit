package ru.practicum.shareit.booking.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mockStatic;

class BookingDtoMapperTest {

    private User booker;
    private Item item;
    private ItemDto itemDto;
    private UserDto userDto;
    private CreateBookingDto newBooking;
    private BookingDto booking;
    private Booking entity;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(3L);
        booker.setName("John Doe");
        booker.setEmail("john@example.com");

        item = new Item();
        item.setId(30L);
        item.setName("Laptop");
        item.setDescription("A powerful laptop");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(null);
        itemDto.setLastBooking(null);
        itemDto.setComments(List.of());

        UserDto userDto = new UserDto();
        userDto.setId(booker.getId());
        userDto.setName(booker.getName());
        userDto.setEmail(booker.getEmail());

        booking = new BookingDto();
        booking.setId(100L);
        booking.setStart(LocalDateTime.of(2023, 10, 1, 12, 0));
        booking.setEnd(LocalDateTime.of(2023, 10, 5, 12, 0));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(itemDto);

        newBooking = new CreateBookingDto();
        newBooking.setItemId(booking.getItem().getId());
        newBooking.setStart(booking.getStart());
        newBooking.setEnd(booking.getEnd());

        entity = new Booking();
        entity.setId(booking.getId());
        entity.setStart(booking.getStart());
        entity.setEnd(booking.getEnd());
        entity.setStatus(booking.getStatus());
        entity.setItem(item);
        entity.setBooker(booker);
    }

    @Test
    void mapToBooking_WithBookingDto_ShouldMapAllFieldsCorrectly() {
        Booking result = BookingDtoMapper.mapToBooking(booking, booker, item);

        assertThat(result.getId(), equalTo(booking.getId()));
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getEnd(), equalTo(booking.getEnd()));
        assertThat(result.getStatus(), equalTo(booking.getStatus()));
        assertThat(result.getItem(), sameInstance(item));
        assertThat(result.getBooker(), sameInstance(booker));
    }

    @Test
    void mapToBooking_WithCreateBookingDto_ShouldMapFieldsAndSetWaitingStatus() {
        Booking result = BookingDtoMapper.mapToBooking(newBooking, booker, item);

        // Assert
        assertThat(result.getId(), nullValue());
        assertThat(result.getStart(), equalTo(newBooking.getStart()));
        assertThat(result.getEnd(), equalTo(newBooking.getEnd()));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(result.getItem(), sameInstance(item));
        assertThat(result.getBooker(), sameInstance(booker));
    }

    @Test
    void mapToBookingDto_ShouldMapAllFieldsCorrectly() {
        try (MockedStatic<ItemDtoMapper> itemMapperMock = mockStatic(ItemDtoMapper.class);
            MockedStatic<UserDtoMapper> userMapperMock = mockStatic(UserDtoMapper.class)) {
            itemMapperMock.when(() -> ItemDtoMapper.mapToDto(item)).thenReturn(itemDto);
            userMapperMock.when(() -> UserDtoMapper.mapToUserDto(booker)).thenReturn(userDto);

            BookingDto result = BookingDtoMapper.mapToBookingDto(entity);

            assertThat(result.getId(), equalTo(entity.getId()));
            assertThat(result.getStart(), equalTo(entity.getStart()));
            assertThat(result.getEnd(), equalTo(entity.getEnd()));
            assertThat(result.getStatus(), equalTo(entity.getStatus()));
            assertThat(result.getItem(), sameInstance(itemDto));
            assertThat(result.getBooker(), sameInstance(userDto));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}