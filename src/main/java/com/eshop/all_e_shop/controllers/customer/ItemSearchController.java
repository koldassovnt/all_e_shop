package com.eshop.all_e_shop.controllers.customer;

import com.eshop.all_e_shop.enteties.Brand;
import com.eshop.all_e_shop.enteties.ShopItem;
import com.eshop.all_e_shop.enteties.Users;
import com.eshop.all_e_shop.services.ShopItemService;
import com.eshop.all_e_shop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ItemSearchController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/searchItem")
    public String searchItem(Model model, @RequestParam(name = "search") String search) {
        List<ShopItem> shopItems = shopItemService.getItemsByName(search);
        List<Brand> brands = shopItemService.getAllBrands();

        model.addAttribute("brands", brands);
        model.addAttribute("items", shopItems);
        model.addAttribute("searched_name", search);
        model.addAttribute("currentUser", getUserData());

        return "detailed_search";
    }

    @GetMapping(value = "/ascending")
    public String ascending(Model model, @RequestParam(name = "search") String search) {
        List<ShopItem> shopItems = shopItemService.getItemsByNameAsc(search);
        List<Brand> brands = shopItemService.getAllBrands();

        model.addAttribute("brands", brands);
        model.addAttribute("items", shopItems);
        model.addAttribute("searched_name", search);
        model.addAttribute("currentUser", getUserData());

        return "detailed_search";
    }

    @GetMapping(value = "/descending")
    public String descending(Model model, @RequestParam(name = "search") String search) {
        List<ShopItem> shopItems = shopItemService.getItemsByNameDesc(search);
        List<Brand> brands = shopItemService.getAllBrands();

        model.addAttribute("brands", brands);
        model.addAttribute("items", shopItems);
        model.addAttribute("searched_name", search);
        model.addAttribute("currentUser", getUserData());

        return "detailed_search";
    }

    @GetMapping(value = "/searchItemDetailed")
    public String searchItemDetailed(Model model, @RequestParam(name = "search") String search,
                                     @RequestParam(name = "priceFrom") double priceFrom,
                                     @RequestParam(name = "priceTo") double priceTo) {

        List<ShopItem> shopItems = shopItemService.getItemsByNameAndPriceBetween(search, priceFrom, priceTo);
        List<Brand> brands = shopItemService.getAllBrands();

        model.addAttribute("brands", brands);
        model.addAttribute("items", shopItems);
        model.addAttribute("searched_name", search);
        model.addAttribute("currentUser", getUserData());

        return "detailed_search";
    }

    @GetMapping(value = "/search_by_brand_out/{id}")
    public String searchByBrandOut(Model model, @PathVariable(name = "id") Long id) {

        Brand brand = shopItemService.getBrand(id);
        List<ShopItem> shopItems = shopItemService.getItemsByBrandId(id);
        List<Brand> brands = shopItemService.getAllBrands();

        removeCurrent(brand, brands);

        model.addAttribute("brands", brands);
        model.addAttribute("brand", brand);
        model.addAttribute("items", shopItems);
        model.addAttribute("currentUser", getUserData());

        return "search_by_brand";
    }

    @GetMapping(value = "/search_by_brand_in")
    public String searchByBrandIn(Model model,
                                  @RequestParam(name = "search") String search,
                                  @RequestParam(name = "brand_id") Long id,
                                  @RequestParam(name = "priceFrom") double priceFrom,
                                  @RequestParam(name = "priceTo") double priceTo) {

        List<ShopItem> shopItems = shopItemService.getItemsByBrandIdAndNameAndPriceBetween(id, search, priceFrom, priceTo);
        List<Brand> brands = shopItemService.getAllBrands();
        Brand brand = shopItemService.getBrand(id);

        removeCurrent(brand, brands);

        model.addAttribute("brands", brands);
        model.addAttribute("brand", brand);
        model.addAttribute("items", shopItems);
        model.addAttribute("currentUser", getUserData());

        return "search_by_brand";
    }

    @GetMapping(value = "/ascending_by_brand_search")
    public String ascending_by_brand_search(Model model, @RequestParam(name = "brand_id") Long id) {

        List<ShopItem> shopItems = shopItemService.getItemsByBrandAsc(id);
        List<Brand> brands = shopItemService.getAllBrands();
        Brand brand = shopItemService.getBrand(id);

        removeCurrent(brand, brands);

        model.addAttribute("brands", brands);
        model.addAttribute("brand", brand);
        model.addAttribute("items", shopItems);
        model.addAttribute("currentUser", getUserData());

        return "search_by_brand";
    }

    @GetMapping(value = "/descending_by_brand_search")
    public String descending_by_brand_search(Model model, @RequestParam(name = "brand_id") Long id) {

        List<ShopItem> shopItems = shopItemService.getItemsByBrandDesc(id);
        List<Brand> brands = shopItemService.getAllBrands();
        Brand brand = shopItemService.getBrand(id);

        removeCurrent(brand, brands);

        model.addAttribute("brands", brands);
        model.addAttribute("brand", brand);
        model.addAttribute("items", shopItems);
        model.addAttribute("currentUser", getUserData());

        return "search_by_brand";
    }


    private void removeCurrent(Brand brand, List<Brand> brands){
        int idx=0;
        for (Brand br : brands) {
            if (br.getName().equals(brand.getName()))
                break;

            idx++;
        }

        brands.remove(idx);
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
