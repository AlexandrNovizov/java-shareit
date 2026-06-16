package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;

@JsonTest
public class BookingDtoSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final long itemId = 1;
    private final LocalDateTime start = LocalDateTime.of(2000, 1, 13, 13, 0);
    private final LocalDateTime end = start.plusDays(2);

    private final CreateBookingDto newBooking = new CreateBookingDto(
            itemId,
            start,
            end
    );

    private final String jsonDto = "{\"itemId\":1,\"start\":\"2000-01-13T13:00:00\",\"end\":\"2000-01-15T13:00:00\"}";

    @Test
    void serializeCreateBookingDtoTest() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(newBooking);

        assertThat(json, Matchers.equalTo(jsonDto));
    }

    @Test
    void deserializeCreateBookingDtoTest() throws JsonProcessingException {
        CreateBookingDto dto = objectMapper.readValue(jsonDto, CreateBookingDto.class);

        assertThat(dto.getItemId(), Matchers.equalTo(newBooking.getItemId()));
        assertThat(dto.getStart(), Matchers.equalTo(newBooking.getStart()));
        assertThat(dto.getEnd(), Matchers.equalTo(newBooking.getEnd()));
    }
}
