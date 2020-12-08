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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @Value("${file.itemImg.viewPath}")
    private String viewPath;

    @Value("${file.itemImg.uploadPath}")
    private String uploadPath;

    @Value("${file.itemImg.defaultPicture}")
    private String defaultPicture;


    @Autowired
    ShopItemService shopItemService;

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @GetMapping(value = "/admin_items")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")

    public String admin_item(Model model){
        List<ShopItem> shopItems = shopItemService.getAllItems();
        List<Brand> brands = shopItemService.getAllBrands();

        model.addAttribute("brands", brands);
        model.addAttribute("shopItems", shopItems);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_items";
    }

    @GetMapping(value = "/admin_items_edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String admin_items_edit(Model model, @PathVariable(name = "id") Long id){
        ShopItem shopItem = shopItemService.getItem(id);
        List<Brand> brands = shopItemService.getAllBrands();

        List<Pictures> pictures = shopItemService.getAllPictures();
        List<Pictures> item_pictures = new ArrayList<>();
        for (Pictures p: pictures) {
            if (p.getShopItem().getId().equals(shopItem.getId())){
                item_pictures.add(p);
            }
        }

        List<Categories> categories = shopItemService.getAllCategories();
        List<Categories> item_categories = shopItem.getCategories();
        List<Categories> unsigned_categories = new ArrayList<>(categories);
        unsigned_categories.removeAll(item_categories);

        int idx=0;
        for (Brand br : brands) {
            if (br.getName().equals(shopItem.getBrand().getName()))
                break;

            idx++;
        }

        brands.remove(idx);

        model.addAttribute("item_pictures", item_pictures);
        model.addAttribute("unsigned_categories", unsigned_categories);
        model.addAttribute("item_categories", item_categories);
        model.addAttribute("brands", brands);
        model.addAttribute("shopItem", shopItem);
        model.addAttribute("currentUser", getUserData());


        return "admin/admin_items_edit";
    }

    @PostMapping(value = "/add_item")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String add_item(@RequestParam(name = "name", defaultValue = "") String name,
                           @RequestParam(name = "description", defaultValue = "") String description,
                           @RequestParam(name = "price", defaultValue = "0") double price,
                           @RequestParam(name = "brand_id", defaultValue = "0L") Long brand_id,
                           @RequestParam(name = "stars", defaultValue = "0") int stars,
                           @RequestParam(name = "small_pictureURL", defaultValue = "") String small_pictureURL,
                           @RequestParam(name = "large_pictureURL", defaultValue = "") String large_pictureURL,
                           @RequestParam(value = "to_top", required = false) String to_top){
        boolean isTop;
        isTop= to_top.equals("YES");

        Brand brand = shopItemService.getBrand(brand_id);

        if (brand != null) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            ShopItem sh = new ShopItem();
            sh.setName(name);
            sh.setDescription(description);
            sh.setPrice(price);
            sh.setBrand(brand);
            sh.setStars(stars);
            sh.setSmallPicURL(small_pictureURL);
            sh.setLargePicURL(large_pictureURL);
            sh.setInTopPage(isTop);
            sh.setAddedDate(timestamp);

            shopItemService.addItem(sh);
        }

        return "redirect:/admin_items";
    }

    @PostMapping(value = "/delete_item")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String delete_item(@RequestParam(name = "item_id", defaultValue = "0L") Long item_id){

        ShopItem shopItem = shopItemService.getItem(item_id);
        shopItemService.deleteItem(shopItem);

        return "redirect:/admin_items";
    }

    @PostMapping(value = "/edit_item")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String admin_item(@RequestParam(name = "name", defaultValue = "") String name,
                          @RequestParam(name = "description", defaultValue = "") String description,
                          @RequestParam(name = "price", defaultValue = "0") double price,
                          @RequestParam(name = "brand_id", defaultValue = "0L") Long brand_id,
                          @RequestParam(name = "item_id", defaultValue = "0L") Long item_id,
                          @RequestParam(name = "stars", defaultValue = "0") int stars,
                          @RequestParam(name = "small_pictureURL", defaultValue = "") String small_pictureURL,
                          @RequestParam(name = "large_pictureURL", defaultValue = "") String large_pictureURL,
                          @RequestParam(value = "to_top", required = false) String to_top){

        boolean isTop;
        isTop= to_top.equals("YES");

        Brand brand = shopItemService.getBrand(brand_id);
        ShopItem sh = shopItemService.getItem(item_id);

        if (brand != null) {
            sh.setName(name);
            sh.setDescription(description);
            sh.setPrice(price);
            sh.setBrand(brand);
            sh.setStars(stars);
            sh.setSmallPicURL(small_pictureURL);
            sh.setLargePicURL(large_pictureURL);
            sh.setInTopPage(isTop);

            shopItemService.saveItem(sh);
        }

        return "redirect:/admin_items";
    }

    @PostMapping(value = "/assign_category")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String assignCategory(@RequestParam(name = "cat_id") Long cat_id,
                                 @RequestParam(name = "item_id") Long item_id){

        Categories cat = shopItemService.getCategory(cat_id);
        if (cat != null) {
            ShopItem sh = shopItemService.getItem(item_id);
            if (sh != null) {
                List<Categories> categories = sh.getCategories();
                if (categories == null)
                    categories = new ArrayList<>();

                categories.add(cat);

                shopItemService.saveItem(sh);
                return "redirect:/admin_items_edit/" + item_id;
            }
        }

        return "redirect:/admin_items";
    }

    @PostMapping(value = "/unassign_category")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String unassignCategory(@RequestParam(name = "cat_id") Long cat_id,
                                 @RequestParam(name = "item_id") Long item_id){

        Categories cat = shopItemService.getCategory(cat_id);
        if (cat != null) {
            ShopItem sh = shopItemService.getItem(item_id);
            if (sh != null) {
                int idx = 0;
                for(Categories c : sh.getCategories()) {
                    if (c.getId().equals(cat.getId()))
                        break;
                    idx++;
                }
                sh.getCategories().remove(idx);

                shopItemService.saveItem(sh);
                return "redirect:/admin_items_edit/" + item_id;
            }
        }

        return "redirect:/admin_items";
    }

    @PostMapping(value = "/add_item_picture")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addItemPicture(@RequestParam(name = "image") MultipartFile file,
                                 @RequestParam(name = "item_id") Long item_id) {
        if (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")) {
            try {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String picName = DigestUtils.sha1Hex("item_pic" + item_id + timestamp.toString());

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                ShopItem sh = shopItemService.getItem(item_id);

                Pictures picture = new Pictures();
                picture.setDate(timestamp);
                picture.setShopItem(sh);
                picture.setUrl(picName);

                shopItemService.savePicture(picture);
                return "redirect:/admin_items_edit/" + item_id;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/admin_items";
    }

    @GetMapping(value = "/viewItemImg/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public @ResponseBody byte[] viewItemImg(@PathVariable(name = "url") String url) throws IOException {

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

    @PostMapping(value = "/unassign_picture")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String unassign_picture(@RequestParam(name = "pic_id") Long pic_id,
                                   @RequestParam(name = "item_id") Long item_id){

        Pictures picture = shopItemService.getPicture(pic_id);
        shopItemService.deletePicture(picture);

        return "redirect:/admin_items_edit/" + item_id;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping(value = "/admin_countries")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_countries(Model model){
        List<Country> countries = shopItemService.getAllCountries();

        model.addAttribute("countries", countries);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_countries";
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping(value = "/admin_users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_users(Model model){
        List<Users> users = userService.getAllUsers();
        List<Role> roles = userService.getAllRoles();

        model.addAttribute("roles", roles);
        model.addAttribute("users", users);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_users";
    }

    @PostMapping(value = "/add_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String add_user(@RequestParam(name = "user_email", defaultValue = "") String user_email,
                           @RequestParam(name = "user_password", defaultValue = "") String user_password,
                           @RequestParam(name = "role_id", defaultValue = "0") Long role_id,
                           @RequestParam(name = "user_name", defaultValue = "") String user_name){

        Role role = userService.getRole(role_id);
        if (role != null) {
            Users users = new Users();
            users.setFullName(user_name);
            users.setEmail(user_email);
            users.setPassword(passwordEncoder.encode(user_password));

            List<Role> roles;

            if (role.getName().equals("ROLE_ADMIN")) {
               roles = userService.getAllRoles();
            } else if (role.getName().equals("ROLE_MODERATOR")) {
                Role userRole = userService.getRoleByRoleName("ROLE_USER");
                roles = new ArrayList<>();
                roles.add(role);
                roles.add(userRole);
            } else {
                roles = new ArrayList<>();
                roles.add(role);
            }

            users.setRoles(roles);
            userService.addUser(users);
        }

        return "redirect:/admin_users";
    }

    @GetMapping(value = "/admin_users_edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_users_edit(Model model, @PathVariable(name = "id") Long id){
        Users users = userService.getUser(id);
        Role userFirstRole = users.getRoles().get(0);
        List<Role> roles = userService.getAllRoles();

        int idx = 0;
        for(Role r : roles) {

            if (r.getId().equals(userFirstRole.getId()))
                break;
            idx++;
        }
        roles.remove(idx);

        model.addAttribute("roles", roles);
        model.addAttribute("user", users);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_users_edit";
    }

    @PostMapping(value = "/edit_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String edit_user(@RequestParam(name = "user_email", defaultValue = "") String user_email,
                            @RequestParam(name = "user_password", defaultValue = "") String user_password,
                            @RequestParam(name = "role_id", defaultValue = "0") Long role_id,
                            @RequestParam(name = "user_name", defaultValue = "") String user_name,
                            @RequestParam(name = "user_id", defaultValue = "0") Long user_id) {

        Role role = userService.getRole(role_id);
        if (role != null) {
            Users users = userService.getUser(user_id);
            users.setFullName(user_name);
            users.setEmail(user_email);
            users.setPassword(passwordEncoder.encode(user_password));

            List<Role> roles;

            if (role.getName().equals("ROLE_ADMIN")) {
                roles = userService.getAllRoles();
            } else if (role.getName().equals("ROLE_MODERATOR")) {
                Role userRole = userService.getRoleByRoleName("ROLE_USER");
                roles = new ArrayList<>();
                roles.add(role);
                roles.add(userRole);
            } else {
                roles = new ArrayList<>();
                roles.add(role);
            }

            users.setRoles(roles);
            userService.saveUser(users);
        }

        return "redirect:/admin_users";
    }

    @PostMapping(value = "/delete_user")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_user(@RequestParam(name = "user_id") Long user_id){
        Users users = userService.getUser(user_id);
        userService.deleteUser(users);

        return "redirect:/admin_users";
    }




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping(value = "/admin_roles")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_roles(Model model){
        List<Role> roles = userService.getAllRoles();

        model.addAttribute("roles", roles);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_roles";
    }

    @PostMapping(value = "/add_role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String add_role(@RequestParam(name = "name", defaultValue = "") String name,
                               @RequestParam(name = "description", defaultValue = "") String description){

        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        userService.addRole(role);

        return "redirect:/admin_roles";
    }

    @GetMapping(value = "/admin_roles_edit/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin_roles_edit(Model model, @PathVariable(name = "id") Long id){
        Role role = userService.getRole(id);

        model.addAttribute("role", role);
        model.addAttribute("currentUser", getUserData());

        return "admin/admin_roles_edit";
    }

    @PostMapping(value = "/edit_role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String edit_role(@RequestParam(name = "name") String name,
                             @RequestParam(name = "role_id") Long role_id,
                             @RequestParam(name = "description") String description) {

        Role role = userService.getRole(role_id);
        role.setName(name);
        role.setDescription(description);
        userService.saveRole(role);

        return "redirect:/admin_roles";
    }

    @PostMapping(value = "/delete_role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete_role(@RequestParam(name = "role_id") Long role_id){
        Role role = userService.getRole(role_id);
        userService.deleteRole(role);

        return "redirect:/admin_roles";
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
