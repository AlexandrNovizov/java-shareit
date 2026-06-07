package ru.practicum.shareit.user.dto.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserDtoMapperTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_NAME = "John Doe";
    private static final String TEST_USER_EMAIL = "john.doe@example.com";

    private UserDto userDto;
    private CreateUserDto createUserDto;
    private User user;

    @BeforeEach
    void setup() {
        userDto = new UserDto();
        userDto.setId(TEST_USER_ID);
        userDto.setName(TEST_USER_NAME);
        userDto.setEmail(TEST_USER_EMAIL);

        createUserDto = new CreateUserDto();
        createUserDto.setName(TEST_USER_NAME);
        createUserDto.setEmail(TEST_USER_EMAIL);

        user = new User();
        user.setId(TEST_USER_ID);
        user.setName(TEST_USER_NAME);
        user.setEmail(TEST_USER_EMAIL);
    }

    @Test
    void mapToUser_FromUserDto_ShouldMapAllFieldsCorrectly() {
        User result = UserDtoMapper.mapToUser(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(TEST_USER_ID));
        assertThat(result.getName(), equalTo(TEST_USER_NAME));
        assertThat(result.getEmail(), equalTo(TEST_USER_EMAIL));
    }

    @Test
    void mapToUser_FromCreateUserDto_ShouldMapNameAndEmailCorrectly() {
        User result = UserDtoMapper.mapToUser(createUserDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), nullValue());
        assertThat(result.getName(), equalTo(TEST_USER_NAME));
        assertThat(result.getEmail(), equalTo(TEST_USER_EMAIL));
    }

    @Test
    void mapToUserDto_FromUserEntity_ShouldMapAllFieldsCorrectly() {
        UserDto result = UserDtoMapper.mapToUserDto(user);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(TEST_USER_ID));
        assertThat(result.getName(), equalTo(TEST_USER_NAME));
        assertThat(result.getEmail(), equalTo(TEST_USER_EMAIL));
    }
}