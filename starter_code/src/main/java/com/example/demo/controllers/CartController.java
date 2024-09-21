package com.example.demo.controllers;

import java.util.stream.IntStream;

import com.example.demo.service.CartService;
import com.example.demo.service.ItemService;
import com.example.demo.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    static final Logger logger = LogManager.getLogger(CartController.class);
    private final CartService cartService;
    private final UserService userService;
    private final ItemService itemService;

    public CartController(CartService cartService, UserService userService, ItemService itemService) {
        this.cartService = cartService;
        this.userService = userService;
        this.itemService = itemService;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<?> addTocart(@RequestBody ModifyCartRequest request) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            logger.error("/api/cart/addToCart: Can't add to cart, user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't add to cart, user not found");
        }
        Item item = itemService.getItemById(request.getItemId());
        if (item == null) {
            logger.error("/api/cart/addToCart: Can't add to cart, item not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't add to cart, item not found");
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item));
        Cart savedCart = cartService.save(cart);
        logger.info("/api/cart/addToCart: Item added to cart successfully");
        return ResponseEntity.ok(savedCart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<?> removeFromcart(@RequestBody ModifyCartRequest request) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            logger.error("/api/cart/removeFromCart: Can't remove cart, user not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't remove cart, user not found");
        }
        Item item = itemService.getItemById(request.getItemId());
        if (item == null) {
            logger.error("/api/cart/removeFromCart: Can't remove cart, item not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't remove cart, item not found");
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item));
        Cart savedCart = cartService.save(cart);
        logger.info("/api/cart/removeFromCart: Item removed from cart successfully");
        return ResponseEntity.ok(savedCart);
    }
}
