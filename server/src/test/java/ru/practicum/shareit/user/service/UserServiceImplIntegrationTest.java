package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;
    private User user;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto(
                "other@example.com",
                "Other User"
        );

        updateUserDto = new UpdateUserDto(
                "updated@example.com",
                "Updated Name"
        );

        user = new User(
                null,
                "test@example.com",
                "Test User"
        );
        user = userRepository.save(user);
    }

    @Test
    void create_ShouldSaveUserAndReturnUserDto() {
        UserDto result = userService.create(createUserDto);

        assertThat(result.getName(), equalTo(createUserDto.getName()));
        assertThat(result.getEmail(), equalTo(createUserDto.getEmail()));
        assertThat(result.getId(), notNullValue());
    }

    @Test
    void update_ShouldUpdateUserFieldsAndReturnUpdatedUserDto() {
        UserDto updatedUserDto = userService.update(updateUserDto, user.getId());

        assertThat(updatedUserDto.getId(), equalTo(user.getId()));
        assertThat(updatedUserDto.getName(), equalTo(updateUserDto.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void delete_ShouldRemoveUserFromDatabase() {
        long userId = user.getId();
        userService.delete(userId);

        boolean exists = userRepository.existsById(userId);
        assertThat(exists, Matchers.is(false));
    }

    @Test
    void getById_ShouldReturnUserDtoWhenUserExists() {
        UserDto foundUserDto = userService.getById(user.getId());

        assertThat(foundUserDto.getId(), equalTo(user.getId()));
        assertThat(foundUserDto.getName(), equalTo(user.getName()));
        assertThat(foundUserDto.getEmail(), equalTo(user.getEmail()));
    }

}
