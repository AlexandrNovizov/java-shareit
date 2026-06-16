package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplateBuilder builder;

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    private CreateItemDto createItemDto;
    private UpdateItemDto updateItemDto;
    private CreateCommentDto createCommentDto;
    private final String serverUrl = "http://localhost:9090";
    private static final long VALID_USER_ID = 1L;
    private static final long VALID_ITEM_ID = 2L;
    private static final String QUERY = "test query";

    @BeforeEach
    void setup() {

        when(builder.uriTemplateHandler(any()))
                .thenReturn(builder);

        when(builder.requestFactory(any(Supplier.class)))
                .thenReturn(builder);

        when(builder.build())
                .thenReturn(restTemplate);

        createItemDto = new CreateItemDto();
        updateItemDto = new UpdateItemDto();
        createCommentDto = new CreateCommentDto();
        itemClient = new ItemClient(serverUrl, builder);
    }

    @Test
    void getItem_ShouldCallRestTemplateGet_WithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.getItem(VALID_USER_ID, VALID_ITEM_ID);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/" + VALID_ITEM_ID),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );;
    }

    @Test
    void createItem_ShouldCallRestTemplatePost_WithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.created(null).build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.createItem(VALID_USER_ID, createItemDto);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
            eq(""),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Object.class));
    }

    @Test
    void updateItem_ShouldCallRestTemplatePatch_WithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.updateItem(VALID_USER_ID, VALID_ITEM_ID, updateItemDto);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/" + VALID_ITEM_ID),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getAllByOwnerId_ShouldCallRestTemplateGet_WithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.getAllByOwnerId(VALID_USER_ID);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class));
    }

    @Test
    void search_ShouldCallRestTemplateGet_WithCorrectQueryParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Map<String, Object> parameters = Map.of("text", QUERY);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.search(QUERY);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("?text={text}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class));
    }

    @Test
    void addComment_ShouldCallRestTemplatePost_WithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.created(null).build();
        String url = "/" + VALID_ITEM_ID + "/comment";
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.addComment(VALID_USER_ID, VALID_ITEM_ID, createCommentDto);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class));
    }

}