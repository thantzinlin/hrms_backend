package com.hrms.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hrms.dto.CreateMenuRequest;
import com.hrms.dto.MenuAdminDto;
import com.hrms.dto.MenuDTO;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Menu;
import com.hrms.model.Role;
import com.hrms.model.User;
import com.hrms.repository.MenuRepository;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    // Admin CRUD
    @Transactional
    public MenuAdminDto createMenu(CreateMenuRequest request) {
        Menu menu = new Menu();
        menu.setMenuName(request.getMenuName());
        menu.setModuleCode(request.getModuleCode());
        menu.setUrl(request.getUrl());
        menu.setIcon(request.getIcon());
        menu.setSequence(request.getSequence() != null ? request.getSequence() : 0);
        if (request.getParentId() != null) {
            Menu parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent menu not found with id: " + request.getParentId()));
            menu.setParent(parent);
        }
        Menu savedMenu = menuRepository.save(menu);
        return mapToAdminDto(savedMenu);
    }

    @Transactional(readOnly = true)
    public List<MenuAdminDto> getAllMenusForAdmin() {
        List<Menu> menus = menuRepository.findByIsDeletedFalseOrderBySequenceAsc();
        return buildMenuAdminTree(menus);
    }

    @Transactional(readOnly = true)
    public MenuAdminDto getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
        return mapToAdminDto(menu);
    }

    @Transactional
    public MenuAdminDto updateMenu(Long id, CreateMenuRequest request) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu not found with id: " + id));
        menu.setMenuName(request.getMenuName());
        menu.setModuleCode(request.getModuleCode());
        menu.setUrl(request.getUrl());
        menu.setIcon(request.getIcon());
        menu.setSequence(request.getSequence() != null ? request.getSequence() : 0);
        if (request.getParentId() != null) {
            Menu parent = menuRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent menu not found with id: " + request.getParentId()));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }
        Menu updatedMenu = menuRepository.save(menu);
        return mapToAdminDto(updatedMenu);
    }

    public void deleteMenu(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu not found with id: " + id);
        }
        menuRepository.deleteById(id);
    }

    // Flat list for role-menu assignment (all menu IDs)
    @Transactional(readOnly = true)
    public List<MenuAdminDto> getAllMenusFlat() {
        return menuRepository.findByIsDeletedFalseOrderBySequenceAsc()
                .stream()
                .map(this::mapToAdminDto)
                .toList();
    }

    private MenuAdminDto mapToAdminDto(Menu menu) {
        MenuAdminDto dto = new MenuAdminDto();
        dto.setMenuId(menu.getMenuId());
        dto.setMenuName(menu.getMenuName());
        dto.setModuleCode(menu.getModuleCode());
        dto.setParentId(menu.getParent() != null ? menu.getParent().getMenuId() : null);
        dto.setUrl(menu.getUrl());
        dto.setIcon(menu.getIcon());
        dto.setSequence(menu.getSequence() != null ? menu.getSequence() : 0);
        dto.setChildren(new ArrayList<>());
        return dto;
    }

    private List<MenuAdminDto> buildMenuAdminTree(List<Menu> menus) {
        Map<Long, MenuAdminDto> dtoMap = menus.stream()
                .collect(Collectors.toMap(Menu::getMenuId, this::mapToAdminDto));

        List<MenuAdminDto> roots = new ArrayList<>();
        for (Menu menu : menus) {
            MenuAdminDto currentDto = dtoMap.get(menu.getMenuId());
            if (menu.getParent() != null) {
                MenuAdminDto parentDto = dtoMap.get(menu.getParent().getMenuId());
                if (parentDto != null) {
                    if (parentDto.getChildren() == null) {
                        parentDto.setChildren(new ArrayList<>());
                    }
                    parentDto.getChildren().add(currentDto);
                } else {
                    roots.add(currentDto);
                }
            } else {
                roots.add(currentDto);
            }
        }
        roots.sort(Comparator.comparing(MenuAdminDto::getSequence));
        return roots;
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

        // 1. Convert all Menu -> MenuDTO and store in a map
        Map<Long, MenuDTO> dtoMap = menus.stream()
                .collect(Collectors.toMap(
                        Menu::getMenuId,
                        this::toDto));

        List<MenuDTO> roots = new ArrayList<>();

        // 2. Build parent-child relationship
        for (Menu menu : menus) {
            MenuDTO currentDto = dtoMap.get(menu.getMenuId());

            if (menu.getParent() != null) {
                MenuDTO parentDto = dtoMap.get(menu.getParent().getMenuId());

                // if parent exists, attach child
                if (parentDto != null) {
                    if (parentDto.getChildren() == null) {
                        parentDto.setChildren(new ArrayList<>());
                    }
                    parentDto.getChildren().add(currentDto);
                } else {
                    // parent not found → treat as root
                    roots.add(currentDto);
                }
            } else {
                // no parent → root
                roots.add(currentDto);
            }
        }

        // 3. Sort roots
        roots.sort(Comparator.comparing(MenuDTO::getSequence));

        return roots;
    }

    private MenuDTO toDto(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setMenuId(menu.getMenuId());
        dto.setMenuName(menu.getMenuName());
        dto.setUrl(menu.getUrl());
        dto.setIcon(menu.getIcon());
        dto.setSequence(menu.getSequence());
        dto.setChildren(new ArrayList<>());
        return dto;
    }

    // private List<MenuDTO> toMenuDto(List<Menu> menus) {
    // return menus.stream()
    // .filter(menu -> menu.getParent() == null)
    // .sorted(Comparator.comparing(Menu::getSequence))
    // .map(this::toDto)
    // .toList();
    // }

    // private MenuDTO toDto(Menu menu) {
    // MenuDTO dto = new MenuDTO();
    // dto.setMenuId(menu.getMenuId());
    // dto.setMenuName(menu.getMenuName());
    // dto.setUrl(menu.getUrl());
    // dto.setIcon(menu.getIcon());
    // dto.setSequence(menu.getSequence());

    // if (menu.getChildren() != null) {
    // dto.setChildren(
    // menu.getChildren()
    // .stream()
    // .map(this::toDto)
    // .toList());
    // }
    // return dto;
    // }
}
