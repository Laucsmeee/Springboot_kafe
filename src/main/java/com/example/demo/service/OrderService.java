package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderItemDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public OrderDTO createOrder(OrderDTO dto) {
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setOrderDate(dto.getOrderDate());
        order.setTotalPrice(dto.getTotalPrice());

        List<OrderItem> items = dto.getItems().stream().map(itemDto -> {
            OrderItem item = new OrderItem();
            item.setItemId(itemDto.getItemId());
            item.setQuantity(itemDto.getQuantity());
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        Order saved = orderRepository.save(order);

        return convertToDTO(saved);
    }

    public Optional<OrderDTO> getOrder(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        return orderOpt.map(this::convertToDTO);
    }

    // Новий метод: отримати список замовлень користувача
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setUserId(order.getUserId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalPrice(order.getTotalPrice());

        List<OrderItemDTO> itemsDto = order.getItems().stream().map(item -> {
            OrderItemDTO i = new OrderItemDTO();
            i.setItemId(item.getItemId());
            i.setQuantity(item.getQuantity());
            return i;
        }).collect(Collectors.toList());

        dto.setItems(itemsDto);
        return dto;
    }
}
