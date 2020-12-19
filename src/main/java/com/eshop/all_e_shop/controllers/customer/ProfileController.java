package com.eshop.all_e_shop.controllers.customer;

import com.eshop.all_e_shop.enteties.Brand;
import com.eshop.all_e_shop.enteties.Categories;
import com.eshop.all_e_shop.enteties.Users;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class ProfileController {

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model) {
        List<Brand> brands = shopItemService.getAllBrands();
        List<Categories> categories = shopItemService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);
        model.addAttribute("currentUser", getUserData());

        return "profile";
    }

    @PostMapping(value = "/update_profile")
    public String update_profile(@RequestParam(name = "user_email", defaultValue = "") String user_email,
                                 @RequestParam(name = "user_name", defaultValue = "") String user_name,
                                 @RequestParam(name = "user_id", defaultValue = "0") Long user_id,
                                 RedirectAttributes redirAttrs) {

        Users users = userService.getUser(user_id);
        users.setEmail(user_email);
        users.setFullName(user_name);
        userService.saveUser(users);

        redirAttrs.addFlashAttribute("successfullprofile", "Profile successfully updated");
        return "redirect:/profile";
    }

    @PostMapping(value = "/update_password")
    public String update_password(@RequestParam(name = "user_old_password", defaultValue = "") String user_old_password,
                                  @RequestParam(name = "user_renew_password", defaultValue = "") String user_renew_password,
                                  @RequestParam(name = "user_new_password", defaultValue = "") String user_new_password,
                                  @RequestParam(name = "user_id", defaultValue = "0") Long user_id,
                                  RedirectAttributes redirAttrs) {

        Users user = userService.getUser(user_id);
        String oldPassword = user.getPassword();

        boolean isEqual = passwordEncoder.matches(user_old_password, oldPassword);

        if(!isEqual) {
            redirAttrs.addFlashAttribute("oldpassworderror", "Old Password is invalid!");
            return "redirect:/profile";
        }
        else if (!user_new_password.equals(user_renew_password)) {
            redirAttrs.addFlashAttribute("passwordsnotequal", "New Typed Passwords are not equal");
            return "redirect:/profile";
        }

        user_new_password = passwordEncoder.encode(user_new_password);
        user.setPassword(user_new_password);

        userService.saveUser(user);

        redirAttrs.addFlashAttribute("successfull", "Password successfully updated");
        return "redirect:/profile";

    }


    @PostMapping(value = "/upload_avatar")
    @PreAuthorize("isAuthenticated()")
    public String upload_avatar(@RequestParam(name = "avatar") MultipartFile file,
                                RedirectAttributes redirAttrs) {

        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
            try {
                Users currentUser = getUserData();

                String picName = DigestUtils.sha1Hex("avatar_" + currentUser.getId() + "Picture!");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                currentUser.setPictureUrl(picName);
                userService.saveUser(currentUser);

                redirAttrs.addFlashAttribute("successfull", "Image successfully uploaded!");
                return "redirect:/profile";

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        redirAttrs.addFlashAttribute("error", "File format is not supported!");
        return "redirect:/profile";
    }

    @GetMapping(value = "/viewphoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    byte[] viewProfileImg(@PathVariable(name = "url") String url) throws IOException {

        String picUrl = viewPath+defaultPicture;

        if (url != null) {
            picUrl = viewPath+url+".jpg";
        }
        InputStream in;

        try {
            ClassPathResource resource = new ClassPathResource(picUrl);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return IOUtils.toByteArray(in);
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
