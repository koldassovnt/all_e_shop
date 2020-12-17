package com.eshop.all_e_shop.controllers;

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

        model.addAttribute("item_pictures", item_pictures);
        model.addAttribute("brands", brands);
        model.addAttribute("item", shopItem);
        model.addAttribute("currentUser", getUserData());

        return "detail";
    }

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

        model.addAttribute("items", shopItems);
        model.addAttribute("searched_name", search);
        model.addAttribute("currentUser", getUserData());

        return "detailed_search";
    }

    @GetMapping(value = "/descending")
    public String descending(Model model, @RequestParam(name = "search") String search) {
        List<ShopItem> shopItems = shopItemService.getItemsByNameDesc(search);

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

    @GetMapping(value = "/403")
    public String accessDenied(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "403";
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
        List<Categories> categories = shopItemService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", getUserData());

        return "login";
    }

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

    @PostMapping(value = "/upload_avatar")
    @PreAuthorize("isAuthenticated()")
    public String upload_avatar(@RequestParam(name = "avatar")MultipartFile file,
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
    public @ResponseBody byte[] viewProfileImg(@PathVariable(name = "url") String url) throws IOException {

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
