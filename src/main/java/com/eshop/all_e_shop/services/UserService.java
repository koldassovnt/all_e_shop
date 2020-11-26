package com.eshop.all_e_shop.services;

import com.eshop.all_e_shop.enteties.Role;
import com.eshop.all_e_shop.enteties.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    Users getUserByEmail(String email);
    List<Users> getAllUsers();
    Users addUser(Users users);
    Users saveUser(Users users);
    Users getUser(Long id);
    void deleteUser(Users users);

    List<Role> getAllRoles();
    Role addRole(Role role);
    Role saveRole(Role role);
    Role getRole(Long id);
    void deleteRole(Role role);
    Role getRoleByRoleName(String name);
}
