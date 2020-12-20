package com.eshop.all_e_shop.repositories;

import com.eshop.all_e_shop.enteties.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> getAllByShopItemId(Long id);
}
