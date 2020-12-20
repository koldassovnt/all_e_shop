package com.eshop.all_e_shop.repositories;

import com.eshop.all_e_shop.enteties.Categories;
import com.eshop.all_e_shop.enteties.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {

    List<ShopItem> findAllByInTopPageEqualsOrderByAddedDate(boolean is_top);

    List<ShopItem> findAllByNameContains(String name);

    List<ShopItem> findAllByNameContainsOrderByPriceDesc(String name);

    List<ShopItem> findAllByNameContainsOrderByPriceAsc(String name);

    List<ShopItem> findAllByNameContainsAndPriceBetween(String name, double priceFrom, double priceTo);

    List<ShopItem> findAllByBrandId(Long id);

    List<ShopItem> findAllByBrandIdAndNameContainsAndPriceBetween(Long id, String name, double priceFrom, double priceTo);

    List<ShopItem> findAllByBrandIdOrderByPriceDesc(Long id);

    List<ShopItem> findAllByBrandIdOrderByPriceAsc(Long id);

    List<ShopItem> findAllByCategories(Categories categories);

}
