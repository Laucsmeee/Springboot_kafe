package com.example.demo.service.impl;

import com.example.demo.dto.MenuItemShortDTO;
import com.example.demo.entity.MenuItem;
import com.example.demo.service.MenuItemService;
import com.example.demo.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepo;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepo) {
        this.menuItemRepo = menuItemRepo;
    }

    @Override
    public Map<String, Map<String, List<MenuItemShortDTO>>> getGroupedMenuItems() {
        List<MenuItem> all = menuItemRepo.findAll();

        return all.stream().collect(
                Collectors.groupingBy(
                        item -> item.getCategory().getName(),
                        Collectors.groupingBy(
                                item -> item.getSubCategory().getName(),
                                Collectors.mapping(item -> {
                                    MenuItemShortDTO dto = new MenuItemShortDTO();
                                    dto.setId(item.getItemsId());
                                    dto.setName(item.getName());
                                    dto.setDescription(item.getDescription());
                                    dto.setPrice(item.getPrice());
                                    dto.setPhotoUrl(item.getPhotoUrl());
                                    return dto;
                                }, Collectors.toList())
                        )
                )
        );
    }
}
