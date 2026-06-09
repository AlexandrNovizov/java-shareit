package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestBody CreateBookingDto dto) {

        return bookingClient.bookItem(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {

        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable Long bookingId) {

        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @RequestParam(required = false) BookingState state) {

        if (state == null) {
            state = BookingState.ALL;
        }

        return bookingClient.getAllBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                  @RequestParam(required = false) BookingState state) {

        if (state == null) {
            state = BookingState.ALL;
        }

        return bookingClient.getAllBookingsByOwnerId(ownerId, state);
    }
}
