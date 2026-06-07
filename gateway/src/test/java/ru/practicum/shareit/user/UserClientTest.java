package ru.practicum.shareit.user;

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
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplateBuilder builder;

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    private final String URL = "http://localhost:9090";
    private static final long VALID_USER_ID = 1L;

    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setup() {

        when(builder.uriTemplateHandler(any()))
                .thenReturn(builder);

        when(builder.requestFactory(any(Supplier.class)))
                .thenReturn(builder);

        when(builder.build())
                .thenReturn(restTemplate);


        createUserDto = new CreateUserDto();
        updateUserDto = new UpdateUserDto();

        userClient = new UserClient(URL, builder);
    }

    @Test
    void testCreateUser() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.createUser(createUserDto);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class));
    }

    @Test
    void testGetUser() {
        String url = "/" + VALID_USER_ID;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.getUser(VALID_USER_ID);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class));
    }

    @Test
    void testUpdateUser() {
        String url = "/" + VALID_USER_ID;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.updateUser(VALID_USER_ID, updateUserDto);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(Object.class));
    }

    @Test
    void testDeleteUser() {
        String url = "/" + VALID_USER_ID;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.deleteUser(VALID_USER_ID);

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq(url),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Object.class));
    }
}