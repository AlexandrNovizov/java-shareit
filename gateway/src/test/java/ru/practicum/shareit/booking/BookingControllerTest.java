package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private final long userId = 1L;
    private final long bookingId = 1L;
    private final long itemId = 1L;


    @Test
    void create_ShouldReturnCreated() throws Exception {
        CreateBookingDto dto = new CreateBookingDto(
                itemId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );


        when(bookingClient.bookItem(eq(userId), any(CreateBookingDto.class)))
                .thenReturn(ResponseEntity.created(null).build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(bookingClient).bookItem(eq(userId), any(CreateBookingDto.class));
    }

    @Test
    void approve_ShouldReturnOk() throws Exception {
        when(bookingClient.approveBooking(eq(userId), eq(bookingId), eq(true)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(eq(userId), eq(bookingId), eq(true));
    }

    @Test
    void getById_ShouldReturnOk() throws Exception {
        when(bookingClient.getBooking(eq(userId), eq(bookingId)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(eq(userId), eq(bookingId));
    }

    @Test
    void getAllByUserId_WithoutStateParam_ShouldReturnOk() throws Exception {
        when(bookingClient.getAllBookingsByUserId(eq(userId), eq(BookingState.ALL)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getAllBookingsByUserId(eq(userId), eq(BookingState.ALL));
    }

    @Test
    void getAllByUserId_WithStateParam_ShouldReturnOk() throws Exception {
        BookingState testState = BookingState.CURRENT;
        when(bookingClient.getAllBookingsByUserId(eq(userId), eq(testState)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", testState.name()))
                .andExpect(status().isOk());

        verify(bookingClient).getAllBookingsByUserId(eq(userId), eq(testState));
    }

    @Test
    void getAllByOwnerId_WithoutStateParam_ShouldReturnOk() throws Exception {
        when(bookingClient.getAllBookingsByOwnerId(eq(userId), eq(BookingState.ALL)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getAllBookingsByOwnerId(eq(userId), eq(BookingState.ALL));
    }

    @Test
    void getAllByOwnerId_WithStateParam_ShouldReturnOk() throws Exception {
        BookingState testState = BookingState.PAST;
        when(bookingClient.getAllBookingsByOwnerId(eq(userId), eq(testState)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", testState.name()))
                .andExpect(status().isOk());

        verify(bookingClient).getAllBookingsByOwnerId(eq(userId), eq(testState));
    }
}