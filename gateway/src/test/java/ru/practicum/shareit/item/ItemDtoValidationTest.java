package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

public class ItemDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createCommentDto_WhenTextIsNull_thenHasViolation() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText(null);

        Set<ConstraintViolation<CreateCommentDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void createCommentDto_WhenTextIsEmptyString_thenHasViolation() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("");

        Set<ConstraintViolation<CreateCommentDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void createCommentDto_WhenTextIsEmptyString_thenNoViolation() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("Valid");

        Set<ConstraintViolation<CreateCommentDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }

    @Test
    void createItemDto_WhenNameIsNull_thenHasViolation() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText(null);

        Set<ConstraintViolation<CreateCommentDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenNameIsNull_thenHasViolation() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName(null);
        dto.setDescription("Valid description");
        dto.setAvailable(true);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenNameIsBlank_thenHasViolation() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("   ");
        dto.setDescription("Valid description");
        dto.setAvailable(true);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenDescriptionIsNull_thenHasViolation() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("Valid name");
        dto.setDescription(null);
        dto.setAvailable(true);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenDescriptionIsBlank_thenHasViolation() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("Valid name");
        dto.setDescription("   ");
        dto.setAvailable(true);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenAvailableIsNull_thenHasViolation() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("Valid name");
        dto.setDescription("Valid description");
        dto.setAvailable(null);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("Valid name");
        dto.setDescription("Valid description");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }

    @Test
    void whenRequestIdIsNull_thenNoViolation() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("Valid name");
        dto.setDescription("Valid description");
        dto.setAvailable(true);
        dto.setRequestId(null);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertThat(violations, empty());
    }
}
