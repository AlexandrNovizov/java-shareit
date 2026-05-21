package ru.practicum.shareit.booking.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LastAndNextBooking {
    private Long id;
    private LocalDateTime last;
    private LocalDateTime next;
}
