package com.eshop.all_e_shop.enteties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;


@Entity
@Table(name = "shop_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopItem implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "stars")
    private int stars;

    @Column(name = "amount")
    private int amount;

    @Column(name = "small_picurl")
    private String smallPicURL;

    @Column(name = "large_picurl")
    private String largePicURL;

    @Column(name = "added_date")
    private Timestamp addedDate;

    @Column(name = "in_top_page")
    private boolean inTopPage;

    @ManyToOne(fetch = FetchType.EAGER)
    private Brand brand;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Categories> categories;

    public String priceToStr() {
        return price + "$";
    }


}
