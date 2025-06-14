package com.example.demo.controller;

import com.example.demo.dto.MenuItemShortDTO;
import com.example.demo.service.MenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menuitems")
public class    MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/grouped")
    public ResponseEntity<Map<String, Map<String, List<MenuItemShortDTO>>>> getGroupedItems() {
        return ResponseEntity.ok(menuItemService.getGroupedMenuItems());
    }
}
