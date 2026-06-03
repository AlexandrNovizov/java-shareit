package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto create(CreateBookingDto dto, Long userId);

    BookingDto approve(Long bookingId, Boolean isApproved, Long userId);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAllBookingsByUserId(Long userId, BookingState state);

    List<BookingDto> getAllBookingsByOwnerId(Long ownerId, BookingState state);
}
