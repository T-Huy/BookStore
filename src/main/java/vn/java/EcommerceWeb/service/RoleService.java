package vn.java.EcommerceWeb.service;

import vn.java.EcommerceWeb.model.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAllRolesByUserId(Long userId);
}
