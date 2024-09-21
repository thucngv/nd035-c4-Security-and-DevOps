package com.example.demo.service;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    public UserOrder save(UserOrder order) {
        return orderRepository.save(order);
    }

    public Object findByUser(User user) {
        return orderRepository.findByUser(user);
    }
}
