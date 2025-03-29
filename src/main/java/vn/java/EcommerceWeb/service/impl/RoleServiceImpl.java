package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import vn.java.EcommerceWeb.model.Role;
import vn.java.EcommerceWeb.repository.RoleRepository;
import vn.java.EcommerceWeb.service.RoleService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRolesByUserId(Long userId) {
        List<Role> roles = roleRepository.getAllRolesByUserId(userId);
        return roles;
    }
}
