package com.example.demo.controllers;

import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    static final Logger logger = LogManager.getLogger(OrderController.class);
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }


    @PostMapping("/submit/{username}")
    public ResponseEntity<?> submit(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("/api/order/submit: Can't save order, user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't save order, user not found");
        }
        UserOrder order = UserOrder.createFromCart(user.getCart());
        UserOrder savedOrder = orderService.save(order);
        logger.info("/api/order/submit: Order submitted successfully");
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<?> getOrdersForUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            logger.error("/api/order/history: Can't retrieve order history, user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't retrieve order history, user not found");
        }

        return ResponseEntity.ok(orderService.findByUser(user));
    }
}
