package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderItemDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private UserRepository userRepository;

    public OrderDTO createOrder(OrderDTO dto) {
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setOrderDate(dto.getOrderDate());
        order.setTotalPrice(dto.getTotalPrice());

        List<OrderItem> items = dto.getItems().stream().map(itemDto -> {
            MenuItem menuItem = menuItemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem not found: " + itemDto.getItemId()));
            OrderItem item = new OrderItem();
            item.setMenuItem(menuItem);
            item.setQuantity(itemDto.getQuantity());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);
        Order saved = orderRepository.save(order);
        return convertToDTO(saved);
    }

    public Optional<OrderDTO> getOrder(Long id) {
        return orderRepository.findById(id).map(this::convertToDTO);
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 🔥 Реалізація пошуку по email
    public List<OrderDTO> getOrdersByUserEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return Collections.emptyList();
        Long userId = userOpt.get().getId();
        return getOrdersByUserId(userId);
    }

    public boolean emailExists(String email) {
        List<OrderDTO> orders = getOrdersByUserEmail(email);
        return !orders.isEmpty();
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setUserId(order.getUserId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());

        List<OrderItemDTO> itemsDto = order.getItems().stream().map(item -> {
            OrderItemDTO i = new OrderItemDTO();
            i.setItemName(item.getMenuItem().getName());
            i.setQuantity(item.getQuantity());
            return i;
        }).collect(Collectors.toList());

        dto.setItems(itemsDto);
        return dto;
    }
}
