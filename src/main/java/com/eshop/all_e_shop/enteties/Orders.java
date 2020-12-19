package com.eshop.all_e_shop.enteties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private ShopItem item;

    @Column(name = "amount")
    private int amount;

    @Column(name = "total_price")
    private double total_price;

    @Column(name = "buy_date")
    private Timestamp buy_date;
}
