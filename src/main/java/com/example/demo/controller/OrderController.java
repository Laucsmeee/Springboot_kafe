package com.example.demo.controller;

import com.example.demo.dto.EmailDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO created = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        Optional<OrderDTO> orderOpt = orderService.getOrder(id);
        return orderOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        List<OrderDTO> orders = orderService.getOrdersByUserId(userId);
        if (orders.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<OrderDTO>> getOrdersByEmail(@PathVariable String email) {
        List<OrderDTO> orders = orderService.getOrdersByUserEmail(email);
        if (orders.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(orders);
    }

    // ✅ Перевірка наявності email
    @PostMapping("/check-email")
    public ResponseEntity<String> checkIfEmailExists(@RequestBody EmailDTO emailDTO) {
        boolean exists = orderService.emailExists(emailDTO.getEmail());
        if (exists) {
            return ResponseEntity.ok("User with email exists.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with email not found.");
        }
    }
}
