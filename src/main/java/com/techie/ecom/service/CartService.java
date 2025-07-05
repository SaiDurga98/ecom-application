package com.techie.ecom.service;


import com.techie.ecom.dto.CartItemRequest;
import com.techie.ecom.model.CartItem;
import com.techie.ecom.model.Product;
import com.techie.ecom.model.User;
import com.techie.ecom.repository.CartItemRepository;
import com.techie.ecom.repository.ProductRepository;
import com.techie.ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public boolean addToCart(String userId, CartItemRequest request) {
        //Checking id product exists
        Optional<Product> productObject = productRepository.findById(request.getProductId());
        if(productObject.isEmpty())
            return false;

        Product product = productObject.get();
        //Checking stock
        if(product.getStockQuantity() < request.getQuantity())
            return false;
        //Checking user
        Optional<User> userObject = userRepository.findById(Long.valueOf(userId));
        if(userObject.isEmpty())
            return false;

        User user = userObject.get();
        // Checking is user already has cart item in existing cart
        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);
        if(existingCartItem != null){
            // update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        } else {
            // Create a new cartItem
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setUser(user);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public Boolean deleteItemFromCart(String userId, Long productId) {

        //Checking id product exists
        Optional<Product> productObject = productRepository.findById(productId);
        if (productObject.isEmpty())
            return false;

        //Checking user
        Optional<User> userObject = userRepository.findById(Long.valueOf(userId));
        if (userObject.isEmpty())
            return false;

      if(productObject.isPresent() && userObject.isPresent()){
          cartItemRepository.deleteByUserAndProduct(userObject.get(), productObject.get());
          return true;
      }
      return false;

    }


    public List<CartItem> getCartItems(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepository::findByUser)
                .orElseGet(List::of);
    }

    public void clearCart(String userId) {
         userRepository.findById(Long.valueOf(userId))
                .ifPresent(cartItemRepository::deleteByUser);

    }
}
