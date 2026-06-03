package ru.practicum.shareit.booking.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingDtoMapper {

    public static Booking mapToBooking(BookingDto dto, User booker, Item item) {
        Booking entity = new Booking();

        entity.setId(dto.getId());
        entity.setStart(dto.getStart());
        entity.setEnd(dto.getEnd());
        entity.setStatus(dto.getStatus());
        entity.setItem(item);
        entity.setBooker(booker);

        return entity;
    }

    public static Booking mapToBooking(CreateBookingDto dto, User booker, Item item) {
        Booking entity = new Booking();

        entity.setStart(dto.getStart());
        entity.setEnd(dto.getEnd());
        entity.setStatus(BookingStatus.WAITING);
        entity.setItem(item);
        entity.setBooker(booker);

        return entity;
    }

    public static BookingDto mapToBookingDto(Booking entity) {
        BookingDto dto = new BookingDto();

        dto.setId(entity.getId());
        dto.setStart(entity.getStart());
        dto.setEnd(entity.getEnd());
        dto.setItem(ItemDtoMapper.mapToDto(entity.getItem()));
        dto.setBooker(UserDtoMapper.mapToUserDto(entity.getBooker()));
        dto.setStatus(entity.getStatus());

        return dto;
    }
}
