package com.techie.ecom.dto;


import lombok.*;

@Data
public class CartItemRequest {

    private Long productId;
    private Integer quantity;


}
