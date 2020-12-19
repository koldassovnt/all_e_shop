package com.eshop.all_e_shop.controllers.admin;

import com.eshop.all_e_shop.enteties.Brand;
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
public class AdminBrandController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/admin_brands")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_brand(Model model){
        List<Brand> brands = shopItemService.getAllBrands();
        List<Country> countries = shopItemService.getAllCountries();

        model.addAttribute("countries", countries);
        model.addAttribute("brands", brands);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_brands";
    }

    @GetMapping(value = "/admin_brands_edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_brands_edit(Model model, @PathVariable(name = "id") Long id) {
        Brand brand = shopItemService.getBrand(id);
        List<Country> countries = shopItemService.getAllCountries();

        int idx = 0;
        for(Country c : countries) {
            if (c.getId().equals(brand.getCountry().getId()))
                break;
            idx++;
        }
        countries.remove(idx);

        model.addAttribute("countries", countries);
        model.addAttribute("brand", brand);
        model.addAttribute("currentUser", getUserData());


        return "admin/admin_brands_edit";
    }

    @PostMapping(value = "/add_brand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String add_brand(@RequestParam(name = "name") String name,
                            @RequestParam(name = "country_id") Long country_id) {

        Country country = shopItemService.getCountry(country_id);

        if (country != null) {
            Brand brand = new Brand();
            brand.setName(name);
            brand.setCountry(country);

            shopItemService.addBrand(brand);
        }

        return "redirect:/admin_brands";
    }

    @PostMapping(value = "/edit_brand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String edit_brand(@RequestParam(name = "name") String name,
                             @RequestParam(name = "country_id") Long country_id,
                             @RequestParam(name = "brand_id") Long brand_id) {

        Country country = shopItemService.getCountry(country_id);
        Brand brand = shopItemService.getBrand(brand_id);

        if (country != null) {
            brand.setCountry(country);
            brand.setName(name);

            shopItemService.saveBrand(brand);
        }

        return "redirect:/admin_brands";
    }

    @PostMapping(value = "/delete_brand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_brand(@RequestParam(name = "brand_id") Long brand_id){

        Brand brand = shopItemService.getBrand(brand_id);

        shopItemService.deleteBrand(brand);

        return "redirect:/admin_brands";
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
