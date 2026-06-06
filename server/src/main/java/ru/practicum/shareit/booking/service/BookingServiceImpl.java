package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(CreateBookingDto dto, Long userId) {

        User booker = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId))
        );

        Item item = itemRepository.findById(dto.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format("Предмет с id=%d не найден", dto.getItemId()))
        );

        if (!item.getAvailable()) {
            throw new UnavailableItemException(String.format("Предмет с id=%d недоступен", item.getId()));
        }

        validateBooking(dto);

        Booking bookingToCreate = BookingDtoMapper.mapToBooking(dto, booker, item);

        bookingToCreate = bookingRepository.save(bookingToCreate);

        return BookingDtoMapper.mapToBookingDto(bookingToCreate);
    }

    @Override
    public BookingDto approve(Long bookingId, Boolean isApproved, Long userId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование с id=%d не найдено", bookingId))
        );

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException(String.format(
                    "Пользователь с id=%d не является владельцем вещи с id=%d", userId, booking.getItem().getId()
            ));
        }

        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        booking = bookingRepository.save(booking);

        return BookingDtoMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование с id=%d не найдено", bookingId))
        );

        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId))
        );

        if (!userId.equals(booking.getBooker().getId()) &&
                !userId.equals(booking.getItem().getOwner().getId())) {

            throw new AccessDeniedException(String.format(
                    "Пользователю с id=%d запрещен доступ к бронированию с id=%d", userId, bookingId
            ));
        }

        return BookingDtoMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(Long userId, BookingState state) {

        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId))
        );

        List<Booking> bookings = getAllBookingsByUserIdAndState(userId, state);

        return bookings.stream()
                .map(BookingDtoMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(Long ownerId, BookingState state) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", ownerId))
        );

        List<Booking> bookings = getAllBookingsByOwnerIdAndState(ownerId, state);

        return bookings.stream()
                .map(BookingDtoMapper::mapToBookingDto)
                .toList();
    }

    private static void validateBooking(CreateBookingDto dto) {
        if (dto.getStart().equals(dto.getEnd())) {
            throw new ConditionsNotMetException("Время начала бронирования не может быть равно времени конца");
        }

        if (dto.getStart().isAfter(dto.getEnd())) {
            throw new ConditionsNotMetException("Время начала бронирования должно быть до времени конца");
        }
    }

    private List<Booking> getAllBookingsByUserIdAndState(Long userId, BookingState state) {
        switch (state) {
            case ALL:
                return bookingRepository.getAllBookingsByBookerId(userId);
            case PAST:
                return bookingRepository.getPastBookingsByBookerId(userId);
            case CURRENT:
                return bookingRepository.getCurrentBookingsByBookerId(userId);
            case FUTURE:
                return bookingRepository.getFutureBookingsByBookerId(userId);
            case WAITING:
                return bookingRepository.getWaitingBookingsByBookerId(userId);
            case REJECTED:
                return bookingRepository.getRejectedBookingsByBookerId(userId);
            case null, default:
                throw new IllegalArgumentException("Не найден обработчик для state=" + state);
        }
    }

    private List<Booking> getAllBookingsByOwnerIdAndState(Long ownerId, BookingState state) {
        switch (state) {
            case ALL:
                return bookingRepository.getAllBookingsByOwnerId(ownerId);
            case PAST:
                return bookingRepository.getPastBookingsByOwnerId(ownerId);
            case CURRENT:
                return bookingRepository.getCurrentBookingsByOwnerId(ownerId);
            case FUTURE:
                return bookingRepository.getFutureBookingsByOwnerId(ownerId);
            case WAITING:
                return bookingRepository.getWaitingBookingsByOwnerId(ownerId);
            case REJECTED:
                return bookingRepository.getRejectedBookingsByOwnerId(ownerId);
            case null, default:
                throw new IllegalArgumentException("Не найден обработчик для state=" + state);
        }
    }
}
