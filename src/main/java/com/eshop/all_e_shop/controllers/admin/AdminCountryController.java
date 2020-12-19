package com.eshop.all_e_shop.controllers.admin;

import com.eshop.all_e_shop.enteties.Country;
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
public class AdminCountryController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/admin_countries")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_countries(Model model){
        List<Country> countries = shopItemService.getAllCountries();

        model.addAttribute("countries", countries);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_countries";
    }

    @GetMapping(value = "/admin_countries_edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_countries_edit(Model model, @PathVariable(name = "id") Long id) {
        Country country = shopItemService.getCountry(id);

        model.addAttribute("country", country);
        model.addAttribute("currentUser", getUserData());


        return "admin/admin_countries_edit";
    }

    @PostMapping(value = "/add_country")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String add_country(@RequestParam(name = "name") String name,
                              @RequestParam(name = "code") String code) {

        Country country = new Country();
        country.setCode(code);
        country.setName(name);

        shopItemService.addCountry(country);

        return "redirect:/admin_countries";
    }

    @PostMapping(value = "/edit_country")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String edit_country(@RequestParam(name = "name") String name,
                               @RequestParam(name = "code") String code,
                               @RequestParam(name = "country_id") Long country_id) {

        Country country = shopItemService.getCountry(country_id);
        country.setName(name);
        country.setCode(code);

        shopItemService.saveCountry(country);

        return "redirect:/admin_countries";
    }

    @PostMapping(value = "/delete_country")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_country(@RequestParam(name = "country_id") Long country_id){

        Country country = shopItemService.getCountry(country_id);
        shopItemService.deleteCountry(country);

        return "redirect:/admin_countries";
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
