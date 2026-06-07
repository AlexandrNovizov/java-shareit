package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class BookingStateSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        BookingState state = BookingState.CURRENT;

        String json = objectMapper.writeValueAsString(state);

        assertEquals("\"CURRENT\"", json);
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "\"FUTURE\"";

        BookingState state = objectMapper.readValue(json, BookingState.class);

        assertEquals(BookingState.FUTURE, state);
    }
}
