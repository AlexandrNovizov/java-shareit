package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class UserDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createUserDto_whenEmailIsNull_thenHasViolation() {
        CreateUserDto dto = new CreateUserDto();
        dto.setName("John Doe");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void createUserDto_whenEmailIsBlank_thenHasViolation() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("   ");
        dto.setName("John Doe");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        // Blank and Email violations
        assertThat(violations, hasSize(2));
    }

    @Test
    void createUserDto_whenEmailIsInvalid_thenHasViolation() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("invalid-email");
        dto.setName("John Doe");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void createUserDto_whenNameIsNull_thenHasViolation() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void createUserDto_whenNameIsBlank_thenHasViolation() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("john.doe@example.com");
        dto.setName("   ");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void createUserDto_whenValidData_thenNoViolations() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("john.doe@example.com");
        dto.setName("John Doe");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(0));
    }

    @Test
    void updateUserDto_whenEmailIsInvalid_thenHasViolation() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void updateUserDto_whenEmailIsBlank_thenHasViolation() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("   ");
        dto.setName("John Doe");

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(1));
    }

    @Test
    void updateUserDto_whenEmailIsNull_thenNoViolation() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("John Doe");

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(0));
    }

    @Test
    void updateUserDto_whenNameIsNull_thenNoViolation() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(0));
    }

    @Test
    void updateUserDto_whenValidEmail_thenNoViolation() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("john.doe@example.com");

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(0));
    }

    @Test
    void updateUserDto_whenValidData_thenNoViolations() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("john.doe@example.com");
        dto.setName("John Doe");

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertThat(violations, hasSize(0));
    }
}
