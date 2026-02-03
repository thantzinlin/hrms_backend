package com.hrms.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hrms.model.Menu;
import com.hrms.model.Role;
import com.hrms.model.User;
import com.hrms.dto.MenuDTO;
import com.hrms.repository.MenuRepository;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuDTO> getMenusByRoles(
            User user) {
        List<Long> roleIds = user.getRoles()
                .stream()
                .map(Role::getRoleId)
                .toList();

        List<Menu> menus = menuRepository.findMenusByRoles(roleIds);
        return toMenuDto(menus);

    }

    private List<MenuDTO> toMenuDto(List<Menu> menus) {
        return menus.stream()
                .filter(menu -> menu.getParent() == null)
                .sorted(Comparator.comparing(Menu::getSequence))
                .map(this::toDto)
                .toList();
    }

    private MenuDTO toDto(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setMenuId(menu.getMenuId());
        dto.setMenuName(menu.getMenuName());
        dto.setUrl(menu.getUrl());
        dto.setIcon(menu.getIcon());
        dto.setSequence(menu.getSequence());

        if (menu.getChildren() != null) {
            dto.setChildren(
                    menu.getChildren()
                            .stream()
                            .map(this::toDto)
                            .toList());
        }
        return dto;
    }
}
