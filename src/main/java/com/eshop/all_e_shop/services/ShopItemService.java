package com.eshop.all_e_shop.services;

import com.eshop.all_e_shop.enteties.*;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface ShopItemService {

    ShopItem addItem(ShopItem shopItem);
    List<ShopItem> getAllItems();
    List<ShopItem> getOnlyTopItems();
    ShopItem getItem(Long id);
    void deleteItem(ShopItem shopItem);
    ShopItem saveItem(ShopItem shopItem);
    List<ShopItem> getItemsByName(String name);
    List<ShopItem> getItemsByNameAsc(String name);
    List<ShopItem> getItemsByNameDesc(String name);
    List<ShopItem> getItemsByNameAndPriceBetween(String name, double priceFrom, double priceTo);
    List<ShopItem> getItemsByBrandId(Long id);
    List<ShopItem> getItemsByBrandIdAndNameAndPriceBetween(Long id, String name, double priceFrom, double priceTo);
    List<ShopItem> getItemsByBrandAsc(Long id);
    List<ShopItem> getItemsByBrandDesc(Long id);

    List<Country> getAllCountries();
    Country addCountry(Country country);
    Country saveCountry(Country country);
    Country getCountry(Long id);
    void deleteCountry(Country country);

    List<Brand> getAllBrands();
    Brand addBrand(Brand brand);
    Brand saveBrand(Brand brand);
    Brand getBrand(Long id);
    void deleteBrand(Brand brand);

    List<Categories> getAllCategories();
    Categories addCategory(Categories categories);
    Categories saveCategory(Categories categories);
    Categories getCategory(Long id);
    void deleteCategory(Categories categories);

    List<Pictures> getAllPictures();
    Pictures addPicture(Pictures pictures);
    Pictures savePicture(Pictures pictures);
    Pictures getPicture(Long id);
    void deletePicture(Pictures pictures);

    List<Orders> getAllOrders();
    Orders addOrder(Orders orders);
    Orders saveOrder(Orders orders);
    Orders getOrder(Long id);
    void deleteOrder(Orders orders);

    List<Comment> getAllComment();
    Comment addComment(Comment comment);
    Comment saveComment(Comment comment);
    Comment getComment(Long id);
    void deleteComment(Comment comment);
    List<Comment> getCommentsByItemId(Long id);
}
