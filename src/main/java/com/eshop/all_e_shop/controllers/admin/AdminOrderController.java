package com.eshop.all_e_shop.controllers.admin;

import com.eshop.all_e_shop.enteties.Orders;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminOrderController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/admin_orders")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String admin_orders(Model model){
        List<Orders> orders = shopItemService.getAllOrders();

        model.addAttribute("orders", orders);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_orders";
    }

    @PostMapping(value = "/delete_order")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_order(@RequestParam(name = "order_id") Long order_id){
        Orders orders = shopItemService.getOrder(order_id);
        shopItemService.deleteOrder(orders);

        return "redirect:/admin_orders";
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
