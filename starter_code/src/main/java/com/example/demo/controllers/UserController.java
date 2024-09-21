package com.example.demo.controllers;

import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
    static final Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
        if (createUserRequest.getPassword().length() < 7) {
            logger.error("/api/user/create: Password must be more than 7 characters");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Password must be at least 7 characters long");
        }
        if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            logger.error("/api/user/create: Confirm password must match password");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Passwords do not match");
        }

        if (userService.findByUsername(createUserRequest.getUsername()) != null) {
            logger.error("/api/user/create: Username already exists {}", createUserRequest.getUsername());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
        }

        try {
            User user = userService.createUser(createUserRequest.getUsername(), createUserRequest.getPassword());
            logger.info("/api/user/create: User created {}", user.getUsername());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user");
        }
    }
}