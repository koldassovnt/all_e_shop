package com.eshop.all_e_shop.controllers.admin;

import com.eshop.all_e_shop.enteties.Role;
import com.eshop.all_e_shop.enteties.Users;
import com.eshop.all_e_shop.services.ShopItemService;
import com.eshop.all_e_shop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminRoleController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/admin_roles")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_roles(Model model){
        List<Role> roles = userService.getAllRoles();

        model.addAttribute("roles", roles);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_roles";
    }

    @PostMapping(value = "/add_role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String add_role(@RequestParam(name = "name", defaultValue = "") String name,
                           @RequestParam(name = "description", defaultValue = "") String description){

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        userService.addRole(role);

        return "redirect:/admin_roles";
    }

    @GetMapping(value = "/admin_roles_edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_roles_edit(Model model, @PathVariable(name = "id") Long id){
        Role role = userService.getRole(id);

        model.addAttribute("role", role);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_roles_edit";
    }

    @PostMapping(value = "/edit_role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String edit_role(@RequestParam(name = "name") String name,
                            @RequestParam(name = "role_id") Long role_id,
                            @RequestParam(name = "description") String description) {

        Role role = userService.getRole(role_id);
        role.setName(name);
        role.setDescription(description);
        userService.saveRole(role);

        return "redirect:/admin_roles";
    }

    @PostMapping(value = "/delete_role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_role(@RequestParam(name = "role_id") Long role_id){
        Role role = userService.getRole(role_id);
        userService.deleteRole(role);

        return "redirect:/admin_roles";
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
