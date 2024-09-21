package com.example.demo;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.SecurityConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SignupAndLoginTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testAuthorization() {
        String username = "staff";
        String password = "password";
        ResponseEntity<User> response = doCreateUser(username, password);
        assert response.getStatusCode() == HttpStatus.OK;

        User user = response.getBody();
        assert user != null;
        assert user.getUsername().equals(username);

        ResponseEntity<String> responseGetUser = testRestTemplate.getForEntity("http://localhost:" + port + "/api/user/id/" + user.getId(), String.class);
        assert responseGetUser.getStatusCode() == HttpStatus.UNAUTHORIZED;
    }

    @Test
    public void testLogIn() {
        String username = "admin";
        String password = "password";
        ResponseEntity<User> response = doCreateUser(username, password);
        assert response.getStatusCode() == HttpStatus.OK;

        User user = response.getBody();
        assert user != null;
        assert user.getUsername().equals(username);

        ResponseEntity<String> loginResponse = doLogin(username, password);
        assert loginResponse.getStatusCode() == HttpStatus.OK;
        assert loginResponse.getHeaders().get(SecurityConstants.HEADER_STRING) != null;
        assert !Objects.requireNonNull(loginResponse.getHeaders().get(SecurityConstants.HEADER_STRING)).isEmpty();
        assert Objects.requireNonNull(loginResponse.getHeaders().get(SecurityConstants.HEADER_STRING))
                .stream().anyMatch(x -> x.contains(SecurityConstants.TOKEN_PREFIX));
    }

    private ResponseEntity<User> doCreateUser(String username, String password) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(username);
        createUserRequest.setPassword(password);
        createUserRequest.setConfirmPassword(password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<CreateUserRequest> request = new HttpEntity<>(createUserRequest, headers);

        return testRestTemplate.postForEntity("http://localhost:" + port + "/api/user/create", request, User.class);
    }

    private ResponseEntity<String> doLogin(String username, String password) {
        String loginUrl = "http://localhost:" + port + "/login";

        String object = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(object, headers);
        return testRestTemplate.postForEntity(loginUrl, request, String.class);
    }
}
