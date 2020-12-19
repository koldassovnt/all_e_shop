package com.eshop.all_e_shop.controllers.admin;

import com.eshop.all_e_shop.enteties.Role;
import com.eshop.all_e_shop.enteties.Users;
import com.eshop.all_e_shop.services.ShopItemService;
import com.eshop.all_e_shop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminUserController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/admin_users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_users(Model model){
        List<Users> users = userService.getAllUsers();
        List<Role> roles = userService.getAllRoles();

        model.addAttribute("roles", roles);
        model.addAttribute("users", users);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_users";
    }

    @PostMapping(value = "/add_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String add_user(@RequestParam(name = "user_email", defaultValue = "") String user_email,
                           @RequestParam(name = "user_password", defaultValue = "") String user_password,
                           @RequestParam(name = "role_id", defaultValue = "0") Long role_id,
                           @RequestParam(name = "user_name", defaultValue = "") String user_name){

        Role role = userService.getRole(role_id);
        if (role != null) {
            Users users = new Users();
            users.setFullName(user_name);
            users.setEmail(user_email);
            users.setPassword(passwordEncoder.encode(user_password));

            List<Role> roles;

            if (role.getName().equals("ROLE_ADMIN")) {
                roles = userService.getAllRoles();
            } else if (role.getName().equals("ROLE_MODERATOR")) {
                Role userRole = userService.getRoleByRoleName("ROLE_USER");
                roles = new ArrayList<>();
                roles.add(role);
                roles.add(userRole);
            } else {
                roles = new ArrayList<>();
                roles.add(role);
            }

            users.setRoles(roles);
            userService.addUser(users);
        }

        return "redirect:/admin_users";
    }

    @GetMapping(value = "/admin_users_edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_users_edit(Model model, @PathVariable(name = "id") Long id){
        Users users = userService.getUser(id);
        Role userFirstRole = users.getRoles().get(0);
        List<Role> roles = userService.getAllRoles();

        int idx = 0;
        for(Role r : roles) {

            if (r.getId().equals(userFirstRole.getId()))
                break;
            idx++;
        }
        roles.remove(idx);

        model.addAttribute("roles", roles);
        model.addAttribute("user", users);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_users_edit";
    }

    @PostMapping(value = "/edit_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String edit_user(@RequestParam(name = "user_email", defaultValue = "") String user_email,
                            @RequestParam(name = "user_password", defaultValue = "") String user_password,
                            @RequestParam(name = "role_id", defaultValue = "0") Long role_id,
                            @RequestParam(name = "user_name", defaultValue = "") String user_name,
                            @RequestParam(name = "user_id", defaultValue = "0") Long user_id) {

        Role role = userService.getRole(role_id);
        if (role != null) {
            Users users = userService.getUser(user_id);
            users.setFullName(user_name);
            users.setEmail(user_email);
            users.setPassword(passwordEncoder.encode(user_password));

            List<Role> roles;

            if (role.getName().equals("ROLE_ADMIN")) {
                roles = userService.getAllRoles();
            } else if (role.getName().equals("ROLE_MODERATOR")) {
                Role userRole = userService.getRoleByRoleName("ROLE_USER");
                roles = new ArrayList<>();
                roles.add(role);
                roles.add(userRole);
            } else {
                roles = new ArrayList<>();
                roles.add(role);
            }

            users.setRoles(roles);
            userService.saveUser(users);
        }

        return "redirect:/admin_users";
    }

    @PostMapping(value = "/delete_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_user(@RequestParam(name = "user_id") Long user_id){
        Users users = userService.getUser(user_id);
        userService.deleteUser(users);

        return "redirect:/admin_users";
    }

    private Users getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authentication.getPrincipal();
            return userService.getUserByEmail(secUser.getUsername());
        }
        return null;
    }

}
