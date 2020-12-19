package com.eshop.all_e_shop.controllers.admin;


import com.eshop.all_e_shop.enteties.*;
import com.eshop.all_e_shop.services.ShopItemService;
import com.eshop.all_e_shop.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminCategoryController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String adminIndex(Model model){
        List<Categories> categories = shopItemService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin";
    }

    @PostMapping(value = "/add_category")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String add_category(@RequestParam(name = "name", defaultValue = "") String name,
                           @RequestParam(name = "logo", defaultValue = "") String logo){

        Categories categories = new Categories();
        categories.setName(name);
        categories.setLogoURL(logo);

        shopItemService.addCategory(categories);

        return "redirect:/admin";
    }

    @GetMapping(value = "/admin_categories_edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_categories_edit(Model model, @PathVariable(name = "id") Long id) {
        Categories categories = shopItemService.getCategory(id);

        model.addAttribute("category", categories);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_categories_edit";
    }

    @PostMapping(value = "/edit_category")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String edit_category(@RequestParam(name = "name", defaultValue = "") String name,
                                @RequestParam(name = "logo", defaultValue = "") String logo,
                                @RequestParam(name = "category_id") Long category_id) {

        Categories categories = shopItemService.getCategory(category_id);

        categories.setLogoURL(logo);
        categories.setName(name);

        shopItemService.saveCategory(categories);

        return "redirect:/admin";
    }

    @PostMapping(value = "/delete_category")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_category(@RequestParam(name = "category_id") Long category_id){

        Categories categories = shopItemService.getCategory(category_id);
        shopItemService.deleteCategory(categories);

        return "redirect:/admin";
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
