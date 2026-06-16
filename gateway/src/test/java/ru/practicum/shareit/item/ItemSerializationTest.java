package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class ItemSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCommentDtoSerialization() throws Exception {
        CreateCommentDto dto = new CreateCommentDto("Test comment text");

        String json = objectMapper.writeValueAsString(dto);

        assertEquals("{\"text\":\"Test comment text\"}", json);
    }

    @Test
    void testCreateCommentDtoDeserialization() throws Exception {
        String json = "{\"text\":\"Another test comment\"}";

        CreateCommentDto dto = objectMapper.readValue(json, CreateCommentDto.class);

        assertNotNull(dto);
        assertEquals("Another test comment", dto.getText());
    }

    @Test
    void testCreateItemDtoSerialization() throws Exception {
        CreateItemDto dto = new CreateItemDto(
                "Item name",
                "Item description",
                true,
                123L
        );

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"name\":\"Item name\""));
        assertTrue(json.contains("\"description\":\"Item description\""));
        assertTrue(json.contains("\"available\":true"));
        assertTrue(json.contains("\"requestId\":123"));
    }

    @Test
    void testCreateItemDtoDeserialization() throws Exception {
        String json = "{" +
                "\"name\": \"Deserialized item\"," +
                "\"description\": \"Deserialized description\"," +
                "\"available\": false," +
                "\"requestId\": 456" +
                "}";

        CreateItemDto dto = objectMapper.readValue(json, CreateItemDto.class);

        assertNotNull(dto);
        assertEquals("Deserialized item", dto.getName());
        assertEquals("Deserialized description", dto.getDescription());
        assertFalse(dto.getAvailable());
        assertEquals(456L, dto.getRequestId());
    }

    @Test
    void testUpdateItemDtoSerialization() throws Exception {
        UpdateItemDto dto = new UpdateItemDto(
                "Updated name",
                "Updated description",
                false
        );

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"name\":\"Updated name\""));
        assertTrue(json.contains("\"description\":\"Updated description\""));
        assertTrue(json.contains("\"available\":false"));
    }

    @Test
    void testUpdateItemDtoDeserialization() throws Exception {
        String json = "{" +
                "\"name\": \"Parsed name\"," +
                "\"description\": \"Parsed description\"," +
                "\"available\": true" +
                "}";

        UpdateItemDto dto = objectMapper.readValue(json, UpdateItemDto.class);

        assertNotNull(dto);
        assertEquals("Parsed name", dto.getName());
        assertEquals("Parsed description", dto.getDescription());
        assertTrue(dto.getAvailable());
    }

}
