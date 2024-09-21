package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.service.OrderService;
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

import java.util.ArrayList;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class OrderControllerTests {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private OrderService orderService;

    private OrderController _orderController;

    private OrderController getController() {
        if (_orderController == null) {
            _orderController = new OrderController(orderService, userService);
        }
        return _orderController;
    }

    @BeforeEach
    public void setUp() {
        // Initialize the Mockito annotations
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSubmitOrderSuccess() {
        User user = new User();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(0));
        user.setCart(cart);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(orderRepository.save(any())).thenReturn(new com.example.demo.model.persistence.UserOrder());
        ResponseEntity<?> response = getController().submit("test");
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    public void testSubmitOrderFail() {
        when(userRepository.findByUsername(any())).thenReturn(null);
        ResponseEntity<?> response = getController().submit("test");
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assert Objects.requireNonNull((String) response.getBody()).equals("Can't save order, user not found");
    }

    @Test
    public void testGetOrdersForUserSuccess(){
        User user = new User();
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(orderRepository.findByUser(any())).thenReturn(new ArrayList<>(0));
        ResponseEntity<?> response = getController().getOrdersForUser("test");
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    public void testGetOrdersForUserFail(){
        when(userRepository.findByUsername(any())).thenReturn(null);
        ResponseEntity<?> response = getController().getOrdersForUser("test");
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assert Objects.requireNonNull((String) response.getBody()).equals("Can't retrieve order history, user not found");
    }
}
