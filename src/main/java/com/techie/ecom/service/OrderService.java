package com.techie.ecom.service;

import com.techie.ecom.dto.OrderItemDTO;
import com.techie.ecom.dto.OrderResponse;
import com.techie.ecom.model.*;
import com.techie.ecom.repository.OrderRepository;
import com.techie.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    public Optional<OrderResponse> createOrder(String userId) {
        // Validate for cart Items
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }
        // Validate the user
        Optional<User> userOptional =userRepository.findById(Long.valueOf(userId));
        if(userOptional.isEmpty()){
            return Optional.empty();
        }
        User user = userOptional.get();
        // Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                        .map(CartItem::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalPrice);
        order.setStatus(OrderStatus.CONFIRMED);
        List<OrderItem> orderItems = cartItems.stream()
                        .map( cartItem -> new OrderItem(
                                null,
                                cartItem.getProduct(),
                                cartItem.getQuantity(),
                                cartItem.getPrice(),
                                order
                        )).toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        // Clear the cart
        cartService.clearCart(userId);

        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private OrderResponse mapToOrderResponse(Order savedOrder) {
       return new OrderResponse(
               savedOrder.getId(),
               savedOrder.getTotalAmount(),
               savedOrder.getStatus(),
               savedOrder.getItems().stream()
                       .map(orderItem -> new OrderItemDTO(
                               orderItem.getId(),
                               orderItem.getProduct().getId(),
                               orderItem.getQuantity(),
                               orderItem.getPrice(),
                               orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                       )).toList(),
               savedOrder.getCreatedAt()
       );
    }
}
