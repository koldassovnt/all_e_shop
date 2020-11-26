package com.eshop.all_e_shop.enteties;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    public String printRoles() {
        StringBuilder res = new StringBuilder();

        for (Role r : this.roles) {
            res.append(r.getName()).append(", ");
        }

        return res.toString();
    }
}
