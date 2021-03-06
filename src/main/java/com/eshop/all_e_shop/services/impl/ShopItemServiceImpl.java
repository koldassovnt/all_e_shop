package com.eshop.all_e_shop.services.impl;

import com.eshop.all_e_shop.enteties.*;
import com.eshop.all_e_shop.repositories.*;
import com.eshop.all_e_shop.services.ShopItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopItemServiceImpl implements ShopItemService {

    @Autowired
    ShopItemRepository shopItemRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    CategoriesRepository categoriesRepository;

    @Autowired
    PictureRepository pictureRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CommentRepository commentRepository;

    @Override
    public ShopItem addItem(ShopItem shopItem) {
        return shopItemRepository.save(shopItem);
    }

    @Override
    public List<ShopItem> getAllItems() {
        return shopItemRepository.findAll();
    }

    @Override
    public List<ShopItem> getOnlyTopItems() {
        return shopItemRepository.findAllByInTopPageEqualsOrderByAddedDate(true);
    }

    @Override
    public ShopItem getItem(Long id) {
        return shopItemRepository.getOne(id);
    }

    @Override
    public void deleteItem(ShopItem shopItem) {
        shopItemRepository.delete(shopItem);
    }

    @Override
    public ShopItem saveItem(ShopItem shopItem) {
        return shopItemRepository.save(shopItem);
    }

    @Override
    public List<ShopItem> getItemsByName(String name) {
        return shopItemRepository.findAllByNameContains(name);
    }

    @Override
    public List<ShopItem> getItemsByNameAsc(String name) {
        return shopItemRepository.findAllByNameContainsOrderByPriceAsc(name);
    }

    @Override
    public List<ShopItem> getItemsByNameDesc(String name) {
        return shopItemRepository.findAllByNameContainsOrderByPriceDesc(name);
    }

    @Override
    public List<ShopItem> getItemsByNameAndPriceBetween(String name, double priceFrom, double priceTo) {
        return shopItemRepository.findAllByNameContainsAndPriceBetween(name, priceFrom, priceTo);
    }

    @Override
    public List<ShopItem> getItemsByBrandId(Long id) {
        return shopItemRepository.findAllByBrandId(id);
    }

    @Override
    public List<ShopItem> getItemsByBrandIdAndNameAndPriceBetween(Long id, String name, double priceFrom, double priceTo) {
        return shopItemRepository.findAllByBrandIdAndNameContainsAndPriceBetween(id, name, priceFrom, priceTo);
    }

    @Override
    public List<ShopItem> getItemsByBrandAsc(Long id) {
        return shopItemRepository.findAllByBrandIdOrderByPriceAsc(id);
    }

    @Override
    public List<ShopItem> getItemsByBrandDesc(Long id) {
        return shopItemRepository.findAllByBrandIdOrderByPriceDesc(id);
    }

    @Override
    public List<ShopItem> getItemsByCategories(Categories categories) {
        return shopItemRepository.findAllByCategories(categories);
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Country addCountry(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public Country saveCountry(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public Country getCountry(Long id) {
        return countryRepository.getOne(id);
    }

    @Override
    public void deleteCountry(Country country) {
        countryRepository.delete(country);
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brand addBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Brand getBrand(Long id) {
        return brandRepository.getOne(id);
    }

    @Override
    public void deleteBrand(Brand brand) {
        brandRepository.delete(brand);
    }

    @Override
    public List<Categories> getAllCategories() {
        return categoriesRepository.findAll();
    }

    @Override
    public Categories addCategory(Categories categories) {
        return categoriesRepository.save(categories);
    }

    @Override
    public Categories saveCategory(Categories categories) {
        return categoriesRepository.save(categories);
    }

    @Override
    public Categories getCategory(Long id) {
        return categoriesRepository.getOne(id);
    }

    @Override
    public void deleteCategory(Categories categories) {
        categoriesRepository.delete(categories);
    }

    @Override
    public List<Pictures> getAllPictures() {
        return pictureRepository.findAll();
    }

    @Override
    public Pictures addPicture(Pictures pictures) {
        return pictureRepository.save(pictures);
    }

    @Override
    public Pictures savePicture(Pictures pictures) {
        return pictureRepository.save(pictures);
    }

    @Override
    public Pictures getPicture(Long id) {
        return pictureRepository.getOne(id);
    }

    @Override
    public void deletePicture(Pictures pictures) {
        pictureRepository.delete(pictures);
    }

    @Override
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Orders addOrder(Orders orders) {
        return orderRepository.save(orders);
    }

    @Override
    public Orders saveOrder(Orders orders) {
        return orderRepository.save(orders);
    }

    @Override
    public Orders getOrder(Long id) {
        return orderRepository.getOne(id);
    }

    @Override
    public void deleteOrder(Orders orders) {
        orderRepository.delete(orders);
    }

    @Override
    public List<Comment> getAllComment() {
        return commentRepository.findAll();
    }

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment getComment(Long id) {
        return commentRepository.getOne(id);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> getCommentsByItemId(Long id) {
        return commentRepository.getAllByShopItemId(id);
    }


}
