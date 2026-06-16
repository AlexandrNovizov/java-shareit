package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BaseClient baseClient;

    @BeforeEach
    void setup() {
        baseClient = new BaseClient(restTemplate);
    }


    @Test
    void putTest() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.put("/test", 1L, new Object());

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/test"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void putWithParamsTest() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class)))
                .thenReturn(expectedResponse);

        Map<String, Object> params = Map.of("param", "value");

        ResponseEntity<Object> actualResponse = baseClient.put("/test", 1L, params, new Object());

        assertThat(actualResponse, equalTo(expectedResponse));
        verify(restTemplate).exchange(
                eq("/test"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Object.class),
                eq(params)
        );
    }

    @Test
    void deleteWithUserIdTest() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        ArgumentCaptor<HttpEntity<?>> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = baseClient.delete("/test", 1L);

        assertThat(actualResponse, equalTo(expectedResponse));

        verify(restTemplate).exchange(
                eq("/test"),
                eq(HttpMethod.DELETE),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        HttpHeaders headers = capturedEntity.getHeaders();

        assertThat(headers.containsKey("X-Sharer-User-Id"), equalTo(true));
        assertThat(headers.getFirst("X-Sharer-User-Id"), equalTo("1"));
    }

}
