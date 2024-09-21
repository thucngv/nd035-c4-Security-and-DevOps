package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.service.CartService;
import com.example.demo.service.ItemService;
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
public class CartControllerTests {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private ItemService itemService;

    @InjectMocks
    private CartService cartService;

    private CartController _cartController;

    private CartController getController() {
        if (_cartController == null) {
            _cartController = new CartController(cartService, userService, itemService);
        }
        return _cartController;
    }

    @BeforeEach
    public void setUp() {
        // Initialize the Mockito annotations
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddToCartSuccess() {
        User user = new User();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(0));
        user.setCart(cart);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(java.util.Optional.of(new Item()));
        when(cartRepository.save(any())).thenReturn(new Cart());

        ResponseEntity<?> response = getController().addTocart(new ModifyCartRequest());
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    public void testAddToCartFailUserNotFound() {
        when(userRepository.findByUsername(any())).thenReturn(null);

        ResponseEntity<?> response = getController().addTocart(new ModifyCartRequest());
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assert Objects.requireNonNull((String) response.getBody()).equals("Can't add to cart, user not found");
    }

    @Test
    public void testAddToCartFailItemNotFound() {
        User user = new User();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(0));
        user.setCart(cart);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(java.util.Optional.empty());
        ResponseEntity<?> response = getController().addTocart(new ModifyCartRequest());
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assert Objects.requireNonNull((String) response.getBody()).equals("Can't add to cart, item not found");
    }

    @Test
    public void testRemoveFromCartSuccess(){
        User user = new User();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(0));
        user.setCart(cart);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(java.util.Optional.of(new Item()));
        when(cartRepository.save(any())).thenReturn(new Cart());

        ResponseEntity<?> response = getController().removeFromcart(new ModifyCartRequest());
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    public void testRemoveFromCartFailUserNotFound() {
        when(userRepository.findByUsername(any())).thenReturn(null);

        ResponseEntity<?> response = getController().removeFromcart(new ModifyCartRequest());
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assert Objects.requireNonNull((String) response.getBody()).equals("Can't remove cart, user not found");
    }

    @Test
    public void testRemoveFromCartFailItemNotFound() {
        User user = new User();
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(0));
        user.setCart(cart);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(java.util.Optional.empty());
        ResponseEntity<?> response = getController().removeFromcart(new ModifyCartRequest());
        assert response != null;
        assert response.getStatusCode().equals(HttpStatus.NOT_FOUND);
        assert Objects.requireNonNull((String) response.getBody()).equals("Can't remove cart, item not found");
    }

}
