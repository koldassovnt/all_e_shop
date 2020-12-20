package com.eshop.all_e_shop.controllers.customer;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/")
    public String index(Model model){
        List<ShopItem> shopItems = shopItemService.getAllItems();
        List<Brand> brands = shopItemService.getAllBrands();
        List<Categories> categories = shopItemService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("items",shopItems);
        model.addAttribute("brands", brands);
        model.addAttribute("currentUser", getUserData());

        return "index";
    }


    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable(name = "id") Long id){
        ShopItem shopItem = shopItemService.getItem(id);
        List<Brand> brands = shopItemService.getAllBrands();
        List<Pictures> pictures = shopItemService.getAllPictures();
        List<Pictures> item_pictures = new ArrayList<>();
        for (Pictures p: pictures) {
            if (p.getShopItem().getId().equals(shopItem.getId())){
                item_pictures.add(p);
            }
        }

        List<Comment> comments = shopItemService.getCommentsByItemId(id);

        model.addAttribute("comments", comments);
        model.addAttribute("item_pictures", item_pictures);
        model.addAttribute("brands", brands);
        model.addAttribute("item", shopItem);
        model.addAttribute("currentUser", getUserData());

        return "detail";
    }


    @GetMapping(value = "/403")
    public String accessDenied(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "403";
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
