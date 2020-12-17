package com.eshop.all_e_shop.enteties;

public class Basket {
    private ShopItem item;
    private int amount;
    public Basket(){}

    public Basket(ShopItem item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public ShopItem getItem() {
        return item;
    }

    public void setItem(ShopItem item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
