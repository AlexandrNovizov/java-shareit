package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final ObjectMapper mapper;

    private CreateBookingDto newBooking;
    private UserDto user;
    private ItemDto item;
    private BookingDto bookingDto;
    private List<BookingDto> bookingDtoList;
    private final long userId = 1L;
    private final long itemId = 1L;
    private final long bookingId = 1L;


    @BeforeEach
    void setUp() {
        newBooking = new CreateBookingDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        item = new ItemDto(
                itemId,
                "Test Item",
                "Test Desc",
                true,
                null,
                null,
                List.of()
        );

        user = new UserDto(
                userId,
                "test@mail.com",
                "testName"
        );

        bookingDto = new BookingDto(
                bookingId,
                newBooking.getStart(),
                newBooking.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
        bookingDtoList = List.of(bookingDto);
    }

    @Test
    void create_ShouldReturnCreatedAndBookingDto() throws Exception {
        when(bookingService.create(any(CreateBookingDto.class), eq(userId)))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newBooking))
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    void approve_ShouldReturnBookingDto() throws Exception {
        Boolean approved = true;
        bookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(eq(bookingId), eq(approved), eq(userId)))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString())
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    void getById_ShouldReturnBookingDto() throws Exception {
        when(bookingService.getById(eq(bookingId), eq(userId)))
                .thenReturn(bookingDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void getAllByUserId_WithState_ShouldReturnListOfBookingDto() throws Exception {
        BookingState state = BookingState.CURRENT;
        when(bookingService.getAllBookingsByUserId(eq(userId), eq(state)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(bookingId));
    }

    @Test
    void getAllByUserId_WithoutState_ShouldReturnAllBookings() throws Exception {
        when(bookingService.getAllBookingsByUserId(eq(userId), eq(BookingState.ALL)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllByOwnerId_WithState_ShouldReturnListOfBookingDto() throws Exception {
        Long ownerId = 2L;
        BookingState state = BookingState.FUTURE;
        when(bookingService.getAllBookingsByOwnerId(eq(ownerId), eq(state)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", state.name())
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAllByOwnerId_WithoutState_ShouldReturnAllBookings() throws Exception {
        Long ownerId = 2L;
        when(bookingService.getAllBookingsByOwnerId(eq(ownerId), eq(BookingState.ALL)))
                .thenReturn(bookingDtoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
