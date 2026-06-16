package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplateBuilder builder;

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    private final String serverUrl = "http://localhost:9090";
    private static final long USER_ID = 1L;
    private static final long OWNER_ID = 2L;
    private static final long REQUEST_ID = 3L;
    private static final long ITEM_ID = 4L;

    private CreateItemRequestDto createItemRequestDto;

    @BeforeEach
    void setup() {

        when(builder.uriTemplateHandler(any()))
                .thenReturn(builder);

        when(builder.requestFactory(any(Supplier.class)))
                .thenReturn(builder);

        when(builder.build())
                .thenReturn(restTemplate);


        createItemRequestDto = new CreateItemRequestDto("Test Desc");

        itemRequestClient = new ItemRequestClient(serverUrl, builder);
    }

    @Test
    void createRequest_ShouldCallPostWithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.createRequest(USER_ID, createItemRequestDto);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class));
    }

    @Test
    void getAllByOwnerId_ShouldCallGetWithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.getAllByOwnerId(OWNER_ID);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class));
    }

    @Test
    void getAll_ShouldCallGetWithoutParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.getAll();

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class));
    }

    @Test
    void getRequest_ShouldCallGetWithRequestId() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.getRequest(REQUEST_ID);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/" + REQUEST_ID),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class));
    }

    @Test
    void addItem_ShouldCallPatchWithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemRequestClient.addItem(USER_ID, REQUEST_ID, ITEM_ID);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/" + REQUEST_ID + "/add/" + ITEM_ID),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class));
    }
}