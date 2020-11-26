package com.eshop.all_e_shop.services.impl;

import com.eshop.all_e_shop.enteties.Role;
import com.eshop.all_e_shop.enteties.Users;
import com.eshop.all_e_shop.repositories.RoleRepository;
import com.eshop.all_e_shop.repositories.UsersRepository;
import com.eshop.all_e_shop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Users myUser = usersRepository.findByEmail(s);

        if (myUser != null) {

            return new User(myUser.getEmail(), myUser.getPassword(), myUser.getRoles());
        }

        throw new UsernameNotFoundException("USER NOT FOUND!");

    }

    @Override
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users addUser(Users users) {
        return usersRepository.save(users);
    }

    @Override
    public Users saveUser(Users users) {
        return usersRepository.save(users);
    }

    @Override
    public Users getUser(Long id) {
        return usersRepository.getOne(id);
    }

    @Override
    public void deleteUser(Users users) {
        usersRepository.delete(users);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role getRole(Long id) {
        return roleRepository.getOne(id);
    }

    @Override
    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }

    @Override
    public Role getRoleByRoleName(String name) {
        return roleRepository.getRoleByNameIsLike(name);
    }
}
