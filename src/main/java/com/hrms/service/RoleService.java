package com.hrms.service;

import com.hrms.dto.CreateRoleRequest;
import com.hrms.dto.MenuDTO;
import com.hrms.dto.RoleDto;
import com.hrms.dto.RoleMenuRequest;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Menu;
import com.hrms.model.Role;
import com.hrms.repository.MenuRepository;
import com.hrms.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;

    public RoleService(RoleRepository roleRepository, MenuRepository menuRepository) {
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        if (roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
            throw new IllegalArgumentException("Role already exists with name: " + request.getRoleName());
        }
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setMenus(new HashSet<>());
        Role savedRole = roleRepository.save(role);
        return mapToDto(savedRole);
    }

    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RoleDto> getRolesPage(Pageable pageable) {
        return roleRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return mapToDto(role);
    }

    @Transactional
    public RoleDto updateRole(Long id, CreateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (!role.getRoleName().equals(request.getRoleName()) &&
                roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
            throw new IllegalArgumentException("Role already exists with name: " + request.getRoleName());
        }

        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        Role updatedRole = roleRepository.save(role);
        return mapToDto(updatedRole);
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    @Transactional
    public RoleDto assignMenusToRole(Long roleId, RoleMenuRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        List<Menu> menus = menuRepository.findAllById(request.getMenuIds());
        if (menus.size() != request.getMenuIds().size()) {
            throw new ResourceNotFoundException("One or more menu IDs are invalid");
        }

        role.setMenus(new HashSet<>(menus));
        Role savedRole = roleRepository.save(role);
        return mapToDto(savedRole);
    }

    @Transactional(readOnly = true)
    public List<MenuDTO> getMenusByRoleId(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        List<Menu> menus = new ArrayList<>(role.getMenus());
        return buildMenuTree(menus);
    }

    private RoleDto mapToDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setRoleId(role.getRoleId());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        if (role.getMenus() != null && !role.getMenus().isEmpty()) {
            List<Menu> menus = new ArrayList<>(role.getMenus());
            dto.setMenus(buildMenuTree(menus));
        }
        return dto;
    }

    private List<MenuDTO> buildMenuTree(List<Menu> menus) {
        Map<Long, MenuDTO> dtoMap = menus.stream()
                .collect(Collectors.toMap(Menu::getMenuId, this::toMenuDto));

        List<MenuDTO> roots = new ArrayList<>();
        for (Menu menu : menus) {
            MenuDTO currentDto = dtoMap.get(menu.getMenuId());
            if (menu.getParent() != null) {
                MenuDTO parentDto = dtoMap.get(menu.getParent().getMenuId());
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
        roots.sort(Comparator.comparing(MenuDTO::getSequence));
        return roots;
    }

    private MenuDTO toMenuDto(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setMenuId(menu.getMenuId());
        dto.setMenuName(menu.getMenuName());
        dto.setUrl(menu.getUrl());
        dto.setIcon(menu.getIcon());
        dto.setSequence(menu.getSequence() != null ? menu.getSequence() : 0);
        dto.setChildren(new ArrayList<>());
        return dto;
    }
}
