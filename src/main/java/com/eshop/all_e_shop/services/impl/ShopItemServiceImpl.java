package com.eshop.all_e_shop.services.impl;

import com.eshop.all_e_shop.enteties.Brand;
import com.eshop.all_e_shop.enteties.Categories;
import com.eshop.all_e_shop.enteties.Country;
import com.eshop.all_e_shop.enteties.ShopItem;
import com.eshop.all_e_shop.repositories.BrandRepository;
import com.eshop.all_e_shop.repositories.CategoriesRepository;
import com.eshop.all_e_shop.repositories.CountryRepository;
import com.eshop.all_e_shop.repositories.ShopItemRepository;
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


}
