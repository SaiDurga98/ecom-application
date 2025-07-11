package com.techie.ecom.repository;

import com.techie.ecom.model.CartItem;
import com.techie.ecom.model.Product;
import com.techie.ecom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);

     List<CartItem> findByUser(User user);

    void deleteByUser(User user);
}
