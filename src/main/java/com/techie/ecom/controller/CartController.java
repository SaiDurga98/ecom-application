package com.techie.ecom.controller;


import com.techie.ecom.dto.CartItemRequest;
import com.techie.ecom.model.CartItem;
import com.techie.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest request) {
        if(!cartService.addToCart(userId, request)){
            return ResponseEntity.badRequest().body("Product out of stock or user not found o product not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Product successfully added to the cart");
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<String> removeFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable Long productId) {

        boolean deleted = cartService.deleteItemFromCart(userId, productId);
        return deleted ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(@RequestHeader("X-User-ID") String userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));


    }




}
