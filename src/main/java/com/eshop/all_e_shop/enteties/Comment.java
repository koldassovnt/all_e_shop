package com.eshop.all_e_shop.enteties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "addedDate")
    private Timestamp addedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private ShopItem shopItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users author;

}
