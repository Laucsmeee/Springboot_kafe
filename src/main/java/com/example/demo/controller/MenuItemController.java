package com.example.demo.controller;

import com.example.demo.dto.MenuItemShortDTO;
import com.example.demo.service.MenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menuitems")
@CrossOrigin(origins = "*")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/grouped")
    public ResponseEntity<Map<String, Map<String, List<MenuItemShortDTO>>>> getGroupedMenuItems() {
        Map<String, Map<String, List<MenuItemShortDTO>>> grouped = menuItemService.getGroupedMenuItems();
        return ResponseEntity.ok(grouped);
    }

    @PostMapping
    public ResponseEntity<MenuItemShortDTO> createMenuItem(@RequestBody MenuItemShortDTO dto) {
        MenuItemShortDTO created = menuItemService.createMenuItem(dto);
        return ResponseEntity.ok(created);
    }
}
