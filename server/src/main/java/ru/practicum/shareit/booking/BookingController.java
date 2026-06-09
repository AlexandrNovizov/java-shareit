package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                             @RequestBody CreateBookingDto dto) {

        return bookingService.create(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam Boolean approved) {

        return bookingService.approve(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable Long bookingId) {

        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                           @RequestParam(required = false) BookingState state) {

        if (state == null) {
            state = BookingState.ALL;
        }

        return bookingService.getAllBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwnerId(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                           @RequestParam(required = false) BookingState state) {

        if (state == null) {
            state = BookingState.ALL;
        }

        return bookingService.getAllBookingsByOwnerId(ownerId, state);
    }
}
