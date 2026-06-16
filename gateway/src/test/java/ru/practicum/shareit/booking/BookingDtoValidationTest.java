package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

public class BookingDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenItemIdIsNull_thenHasViolation() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(null);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenStartIsInPast_thenHasViolation() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().minusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(1));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenEndIsInPast_thenHasViolation() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().minusDays(1));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(1L);
        LocalDateTime now = LocalDateTime.now();
        dto.setStart(now.plusDays(1));
        dto.setEnd(now.plusDays(2));

        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }
}
