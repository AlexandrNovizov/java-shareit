package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

public class ItemRequestDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenDescriptionIsNull_thenHasViolation() {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription(null);

        Set<ConstraintViolation<CreateItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenDescriptionIsBlank_thenHasViolation() {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("   ");

        Set<ConstraintViolation<CreateItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenDescriptionIsEmpty_thenHasViolation() {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("");

        Set<ConstraintViolation<CreateItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenDescriptionIsValid_thenNoViolations() {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("Valid description");

        Set<ConstraintViolation<CreateItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }
}
