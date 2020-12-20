package com.eshop.all_e_shop.controllers.customer;

import com.eshop.all_e_shop.enteties.Comment;
import com.eshop.all_e_shop.enteties.ShopItem;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;

@Controller
public class CommentController {

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @PostMapping(value = "/leave_comment")
    @PreAuthorize("isAuthenticated()")
    public String leave_comment(@RequestParam(name = "item_id", defaultValue = "") Long item_id,
                                 @RequestParam(name = "comment", defaultValue = "") String comment_text) {

        Comment comment = new Comment();
        Users users = getUserData();
        ShopItem shopItem = shopItemService.getItem(item_id);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        comment.setComment(comment_text);
        comment.setAuthor(users);
        comment.setAddedDate(timestamp);
        comment.setShopItem(shopItem);

        shopItemService.addComment(comment);

        return "redirect:/detail/" + item_id;
    }

    @PostMapping(value = "/edit_comment")
    public String edit_comment(@RequestParam(name = "item_id", defaultValue = "") Long item_id,
                                @RequestParam(name = "comment_id", defaultValue = "0") Long comment_id,
                               @RequestParam(name = "comment") String comment_text) {

        Comment comment = shopItemService.getComment(comment_id);
        comment.setComment(comment_text);

        shopItemService.saveComment(comment);

        return "redirect:/detail/" + item_id;
    }

    @PostMapping(value = "/delete_comment")
    public String delete_comment(@RequestParam(name = "item_id", defaultValue = "") Long item_id,
                               @RequestParam(name = "comment_id", defaultValue = "0") Long comment_id) {

        Comment comment = shopItemService.getComment(comment_id);
        shopItemService.deleteComment(comment);

        return "redirect:/detail/" + item_id;
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
