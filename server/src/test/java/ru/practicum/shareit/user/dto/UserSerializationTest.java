package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
public class UserSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUserDto_Serialization() throws Exception {
        CreateUserDto dto = new CreateUserDto(
                "test@example.com",
                "John Doe"
        );

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"email\":\"test@example.com\""));
        assertTrue(json.contains("\"name\":\"John Doe\""));
    }

    @Test
    void createUserDto_Deserialization() throws Exception {
        String json = "{\"email\":\"test@example.com\", \"name\":\"John Doe\"}";

        CreateUserDto dto = objectMapper.readValue(json, CreateUserDto.class);

        assertEquals("test@example.com", dto.getEmail());
        assertEquals("John Doe", dto.getName());
    }

    @Test
    void testSerialization() throws Exception {

        UpdateUserDto updateUserDto = new UpdateUserDto(
                "test@example.com",
                "John Doe"
        );

        String json = objectMapper.writeValueAsString(updateUserDto);

        assertTrue(json.contains("\"email\":\"test@example.com\""));
        assertTrue(json.contains("\"name\":\"John Doe\""));
    }

    @Test
    void testDeserialization() throws Exception {
        String json = "{\"email\":\"test@example.com\",\"name\":\"John Doe\"}";

        UpdateUserDto updateUserDto = objectMapper.readValue(json, UpdateUserDto.class);

        assertEquals("test@example.com", updateUserDto.getEmail());
        assertEquals("John Doe", updateUserDto.getName());
    }
}
