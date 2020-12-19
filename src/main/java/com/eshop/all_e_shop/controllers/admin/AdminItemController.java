package com.eshop.all_e_shop.controllers.admin;

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
public class AdminItemController {

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
    public @ResponseBody
    byte[] viewItemImg(@PathVariable(name = "url") String url) throws IOException {

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

    private Users getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authentication.getPrincipal();
            return userService.getUserByEmail(secUser.getUsername());
        }
        return null;
    }

}
