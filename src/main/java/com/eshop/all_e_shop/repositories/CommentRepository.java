package com.eshop.all_e_shop.repositories;

import com.eshop.all_e_shop.enteties.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
