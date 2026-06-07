package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemRequestSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialization() throws Exception {
        CreateItemRequestDto dto = new CreateItemRequestDto(
                "Test description"
        );

        String json = objectMapper.writeValueAsString(dto);

        String expectedJson = "{\"description\":\"Test description\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"description\":\"Test description\"}";

        CreateItemRequestDto dto = objectMapper.readValue(json, CreateItemRequestDto.class);

        assertEquals("Test description", dto.getDescription());
    }
}
