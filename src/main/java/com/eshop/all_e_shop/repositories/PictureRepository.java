package com.eshop.all_e_shop.repositories;

import com.eshop.all_e_shop.enteties.Country;
import com.eshop.all_e_shop.enteties.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PictureRepository extends JpaRepository<Pictures, Long> {
}
