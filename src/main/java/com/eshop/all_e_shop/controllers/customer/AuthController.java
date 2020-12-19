package com.eshop.all_e_shop.controllers.customer;

import com.eshop.all_e_shop.enteties.Categories;
import com.eshop.all_e_shop.enteties.Role;
import com.eshop.all_e_shop.enteties.Users;
import com.eshop.all_e_shop.services.ShopItemService;
import com.eshop.all_e_shop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/login")
    public String login(Model model) {
        List<Categories> categories = shopItemService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", getUserData());

        return "login";
    }

    @GetMapping(value = "/registration")
    public String registration(Model model) {
        List<Categories> categories = shopItemService.getAllCategories();

        model.addAttribute("categories", categories);
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String registration_form(@RequestParam(name = "user_email", defaultValue = "") String user_email,
                                    @RequestParam(name = "user_password", defaultValue = "") String user_password,
                                    @RequestParam(name = "user_re_password", defaultValue = "") String user_re_password,
                                    @RequestParam(name = "user_name", defaultValue = "") String user_name,
                                    RedirectAttributes redirAttrs) {

        if (user_password.equals(user_re_password)) {
            Users users = new Users();
            users.setEmail(user_email);
            users.setPassword(passwordEncoder.encode(user_password));
            users.setFullName(user_name);
            users.setPictureUrl("default-avatar.jpg");
            Role role = userService.getRoleByRoleName("ROLE_USER");
            ArrayList<Role> roles = new ArrayList<>();
            roles.add(role);
            users.setRoles(roles);

            userService.addUser(users);
            return "redirect:/";
        } else {
            redirAttrs.addFlashAttribute("error", "Passwords are not equal");
            return "redirect:/registration";
        }
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
