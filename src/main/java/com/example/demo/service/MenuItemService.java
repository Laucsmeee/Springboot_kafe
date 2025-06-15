package com.example.demo.service;

import com.example.demo.dto.MenuItemShortDTO;
import java.util.List;
import java.util.Map;

public interface MenuItemService {
    Map<String, Map<String, List<MenuItemShortDTO>>> getGroupedMenuItems();
    MenuItemShortDTO createMenuItem(MenuItemShortDTO dto);
}
