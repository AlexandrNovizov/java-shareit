package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    private static final long TEST_USER_ID = 1L;
    private static final long TEST_OWNER_ID = 2L;
    private static final long TEST_BOOKING_ID = 3L;

    @Mock
    private RestTemplateBuilder builder;

    @Mock
    private RestTemplate restTemplate;

    private CreateBookingDto testCreateBookingDto;
    private BookingState testBookingState;
    private BookingClient bookingClient;
    private final String URL = "http://localhost:9090";

    @BeforeEach
    void setup() {

        when(builder.uriTemplateHandler(any()))
                .thenReturn(builder);

        when(builder.requestFactory(any(Supplier.class)))
                .thenReturn(builder);

        when(builder.build())
                .thenReturn(restTemplate);

        testCreateBookingDto = new CreateBookingDto();
        testBookingState = BookingState.ALL;
        bookingClient = new BookingClient(URL, builder);
    }

    @Test
    void getAllBookingsByUserId_ShouldCallRestTemplateWithCorrectParams() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getAllBookingsByUserId(TEST_USER_ID, testBookingState);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("?state={state}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void getAllBookingsByOwnerId_ShouldCallRestTemplateWithCorrectParams() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getAllBookingsByOwnerId(TEST_OWNER_ID, testBookingState);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/owner?state={state}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void approveBooking_ShouldCallRestTemplateWithCorrectParams() {
        boolean approved = true;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.approveBooking(TEST_USER_ID, TEST_BOOKING_ID, approved);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/" + TEST_BOOKING_ID + "?approved={approved}"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void bookItem_ShouldCallRestTemplateWithCorrectParams() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.created(null).build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.bookItem(TEST_USER_ID, testCreateBookingDto);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void getBooking_ShouldCallRestTemplateWithCorrectParams() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getBooking(TEST_USER_ID, TEST_BOOKING_ID);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/" + TEST_BOOKING_ID),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }
}