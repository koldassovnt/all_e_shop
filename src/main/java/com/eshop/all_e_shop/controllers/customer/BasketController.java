package com.eshop.all_e_shop.controllers.customer;

import com.eshop.all_e_shop.enteties.*;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BasketController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/basket")
    public String basket(Model model, HttpSession session) {
        List<Brand> brands = shopItemService.getAllBrands();

        List<Basket> basket = (List<Basket>) session.getAttribute("basket");
        double total = 0;
        if (basket == null){
            basket = new ArrayList<Basket>();
        }
        else {
            total = basket.stream().mapToDouble(e -> e.getAmount() * e.getItem().getPrice()).sum();
        }
        Users currentUser = getUserData();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("basket", basket);
        model.addAttribute("total", total);
        model.addAttribute("brands", brands);
        return "basket";
    }

    @PostMapping(value = "/add_item_to_basket")
    public String add_item_to_basket(@RequestParam(name = "item_id") Long item_id, HttpSession session) {
        List<Basket> basket = (List<Basket>) session.getAttribute("basket");
        if (basket == null) {
            basket = new ArrayList<>();
            session.setAttribute("basket", basket);
        }
        ShopItem shopItem = shopItemService.getItem(item_id);

        if (basket.size() > 0){
            for (Basket b : basket){
                if (b.getItem().getId().equals(item_id)){
                    b.setAmount(b.getAmount() + 1);
                    session.setAttribute("basket", basket);
                    return "redirect:/detail/" + item_id;
                }
            }
        }
        basket.add(new Basket(shopItem, 1));
        session.setAttribute("basket", basket);

        return "redirect:/detail/" + item_id;
    }

    @PostMapping(value = "/add_quantity")
    public String add_quantity(@RequestParam(name = "item_id") Long item_id, HttpSession session) {
        List<Basket> basket = (List<Basket>) session.getAttribute("basket");
        Basket b_item = new Basket();
        ShopItem shopItem = shopItemService.getItem(item_id);

        for(Basket b: basket){
            if(b.getItem().getId().equals(item_id)){
                b.setAmount(b.getAmount() + 1);
                session.setAttribute("baskets", basket);
                return "redirect:/basket";
            }
        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/minus_quantity")
    public String minus_quantity(@RequestParam(name = "item_id") Long item_id, HttpSession session) {
        List<Basket> basket = (List<Basket>) session.getAttribute("basket");
        Basket b_item = new Basket();
        ShopItem shopItem = shopItemService.getItem(item_id);

        for(Basket b: basket){
            if(b.getItem().getId().equals(item_id)){
                if (b.getAmount() > 1){
                    b.setAmount(b.getAmount() - 1);
                }
                else {
                    basket.remove(b);
                }
                session.setAttribute("baskets", basket);
                return "redirect:/basket";
            }
        }
        return "redirect:/basket";
    }

    @PostMapping(value = "/clear_basket")
    public String clear_basket(HttpSession session) {
        session.removeAttribute("basket");

        return "redirect:/basket";
    }

    @PostMapping(value = "/check_in")
    public String check_in(HttpSession session) {
        List<Basket> items = (List<Basket>) session.getAttribute("basket");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        for (Basket b : items) {
            if (b.getItem().getAmount() < b.getAmount() && b.getItem().getAmount() > 0) {
                shopItemService.addOrder(new Orders(null, b.getItem(), b.getItem().getAmount(),b.getItem().getAmount() * b.getItem().getPrice(), timestamp ));
                b.getItem().setAmount(0);
                shopItemService.saveItem(b.getItem());
            } else {
                shopItemService.addOrder(new Orders(null, b.getItem(), b.getAmount(), b.getAmount() * b.getItem().getPrice(), timestamp));

                b.getItem().setAmount(b.getItem().getAmount() - b.getAmount());
                shopItemService.saveItem(b.getItem());
            }
        }

        session.removeAttribute("basket");

        return "redirect:/";
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
