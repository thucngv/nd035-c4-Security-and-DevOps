package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.service.UserService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserControllerTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private UserController _userController;

    private UserController getController() {
        if (_userController == null) {
            _userController = new UserController(userService);
        }
        return _userController;
    }

    @BeforeEach
    public void setUp() {
        // Initialize the Mockito annotations
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindByIdSuccess() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        ResponseEntity<?> response = getController().findById(1L);
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    public void testFindByIdFail() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        ResponseEntity<?> response = getController().findById(2L);
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testFindByUserNameSuccess() {
        String username = "admin";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        when(userRepository.findByUsername(any())).thenReturn(user);
        ResponseEntity<User> response = getController().findByUserName(username);

        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.OK);
        assert Objects.requireNonNull(response.getBody()).getUsername().equals(username);
    }

    @Test
    public void testFindByUserNameFail() {
        String username = "admin";
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        ResponseEntity<User> response = getController().findByUserName(username);

        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testCreateUserSuccess() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("admin");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");

        when(bCryptPasswordEncoder.encode(any())).thenReturn("password");
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<?> response = getController().createUser(createUserRequest);
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.OK);
        assert Objects.requireNonNull((User) response.getBody()).getUsername().equals(createUserRequest.getUsername());
    }

    @Test
    public void testCreateUserFailPasswordLessThan7Character() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("admin");
        createUserRequest.setPassword("x");
        createUserRequest.setConfirmPassword("x");

        ResponseEntity<?> response = getController().createUser(createUserRequest);
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.BAD_REQUEST);
        assert Objects.requireNonNull((String) response.getBody()).equals("Password must be at least 7 characters long");
    }

    @Test
    public void testCreateUserFailPasswordNotMatch() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("admin");
        createUserRequest.setPassword("12345678");
        createUserRequest.setConfirmPassword("123456789");

        ResponseEntity<?> response = getController().createUser(createUserRequest);
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.BAD_REQUEST);
        assert Objects.requireNonNull((String) response.getBody()).equals("Passwords do not match");
    }

    @Test
    public void testCreateUserFailUsernameExists() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("admin");
        createUserRequest.setPassword("12345678");
        createUserRequest.setConfirmPassword("12345678");

        when(bCryptPasswordEncoder.encode(any())).thenReturn("password");
        when(userRepository.findByUsername(any())).thenReturn(new User());

        ResponseEntity<?> response = getController().createUser(createUserRequest);
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.BAD_REQUEST);
        assert Objects.requireNonNull((String) response.getBody()).equals("Username already exists");
    }

    @Test
    public void testCreateUserFailUnExpectedError() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("admin");
        createUserRequest.setPassword("12345678");
        createUserRequest.setConfirmPassword("12345678");

        when(bCryptPasswordEncoder.encode(any())).thenReturn("password");
        when(userRepository.findByUsername(any())).thenReturn(null);
        when(userRepository.save(any())).thenThrow(new RuntimeException("Error creating user"));

        ResponseEntity<?> response = getController().createUser(createUserRequest);
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR);
        assert Objects.requireNonNull((String) response.getBody()).equals("Error creating user");
    }
}
