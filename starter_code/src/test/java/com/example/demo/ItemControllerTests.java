package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.service.ItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ItemControllerTests {
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    private ItemController _controller;

    private ItemController getController() {
        if (_controller == null) {
            _controller = new ItemController(itemService);
        }
        return _controller;
    }

    @Test
    public void testGetItems() {
        List<Item> items = new ArrayList<>();
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = getController().getItems();

        assert response != null;
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() != null;
        assert response.getBody().size() == items.size();
    }

    @Test
    public void testGetItemByIdSuccess() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        ResponseEntity<Item> response = getController().getItemById(1L);

        assert response != null;
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() != null;
        assert Objects.equals(response.getBody().getId(), item.getId());
        assert response.getBody().getName().equals(item.getName());
        assert Objects.equals(response.getBody().getPrice(), item.getPrice());
    }

    @Test
    public void testGetItemByIdNotFound() {
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        ResponseEntity<Item> response = getController().getItemById(1L);

        assert response != null;
        assert response.getStatusCodeValue() == 404;
    }

    @Test
    public void testGetItemsByNameSuccess() {
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        when(itemRepository.findByName("item1")).thenReturn(items);
        ResponseEntity<List<Item>> response = getController().getItemsByName("item1");

        assert response != null;
        assert response.getStatusCodeValue() == 200;
        assert response.getBody() != null;
        assert response.getBody().size() == items.size();
    }
}
