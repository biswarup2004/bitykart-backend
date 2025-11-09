// [file name]: OrderService.java
// [file content begin]
package com.bity.bitykart.service;

import com.bity.bitykart.dto.OrderDto;
import com.bity.bitykart.dto.OrderItemDto;
import com.bity.bitykart.model.OrderItem;
import com.bity.bitykart.model.Orders;
import com.bity.bitykart.model.Product;
import com.bity.bitykart.model.User;
import com.bity.bitykart.repository.OrderRepository;
import com.bity.bitykart.repository.ProductRepository;
import com.bity.bitykart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;
@Transactional
    public OrderDto placeOrder(Long userId, Map<Long, Integer> productQuantities, double totalAmount, String paymentMethod) {
        System.out.println("=== PLACE ORDER START ===");
        System.out.println("User ID: " + userId);
        System.out.println("Product Quantities: " + productQuantities);
        System.out.println("Total Amount: " + totalAmount);
        System.out.println("Payment Method: " + paymentMethod);

        // Step 1: Find user
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            System.out.println("ERROR: User not found with ID: " + userId);
            throw new RuntimeException("user not found");
        }
        User user = userOptional.get();
        System.out.println("User found: " + user.getName() + " (" + user.getEmail() + ")");

        // Step 2: Create order
        Orders order = new Orders();
        order.setUser(user);
        order.setOrderdate(new Date());

        // Set initial status based on payment method
        if ("cod".equalsIgnoreCase(paymentMethod)) {
            order.setStatus("PENDING");
        } else {
            order.setStatus("CONFIRMED");
        }

        order.setTotalAmount(totalAmount);
        System.out.println("Order created with status: " + order.getStatus());

        // Step 3: Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemDto> orderItemDtos = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            System.out.println("Processing product ID: " + productId + ", Quantity: " + quantity);

            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isEmpty()) {
                System.out.println("ERROR: Product not found with ID: " + productId);
                throw new RuntimeException("Product not Found with ID: " + productId);
            }
            Product product = productOptional.get();
            System.out.println("Product found: " + product.getName() + " - ₹" + product.getPrice());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setOrder(order);
            orderItem.setQuantity(quantity);
            orderItems.add(orderItem);

            orderItemDtos.add(new OrderItemDto(product.getName(), product.getPrice(), quantity));

            System.out.println("Order item created: " + product.getName() + " x " + quantity);
        }

        order.setOrderitems(orderItems);
        System.out.println("Total order items: " + orderItems.size());

        // Step 4: Save order
        System.out.println("Saving order to database...");
        try {
            Orders savedOrder = orderRepository.save(order);
            System.out.println("SUCCESS: Order saved with ID: " + savedOrder.getId());
            System.out.println("Order items count in saved order: " + (savedOrder.getOrderitems() != null ? savedOrder.getOrderitems().size() : 0));

            OrderDto result = new OrderDto(savedOrder.getId(), savedOrder.getTotalAmount(), savedOrder.getStatus(),
                    savedOrder.getOrderdate(), orderItemDtos);

            System.out.println("=== PLACE ORDER COMPLETE ===");
            return result;

        } catch (Exception e) {
            System.out.println("ERROR: Failed to save order: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save order: " + e.getMessage());
        }
    }

    public List<OrderDto> getAllOrders() {
        List<Orders> orders = orderRepository.findAllOrdersWithUsers();
        System.out.println("Found " + orders.size() + " total orders in database");
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private OrderDto convertToDTO(Orders orders) {
        List<OrderItemDto> orderItems = orders.getOrderitems().stream()
                .map(item -> new OrderItemDto(
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()))
                .collect(Collectors.toList());

        OrderDto dto = new OrderDto(
                orders.getId(),
                orders.getTotalAmount(),
                orders.getStatus(),
                orders.getOrderdate(),
                orders.getUser() != null ? orders.getUser().getName() : "unknown",
                orders.getUser() != null ? orders.getUser().getEmail() : "unknown",
                orderItems
        );

        System.out.println("Converted order ID: " + orders.getId() + " to DTO");
        return dto;
    }

    public List<OrderDto> getOrderByUser(Long userId) {
        System.out.println("Getting orders for user: " + userId);

        Optional<User> userOp = userRepository.findById(userId);
        if (userOp.isEmpty()) {
            throw new RuntimeException("User not Found");
        }
        User user = userOp.get();
        List<Orders> orders = orderRepository.findByUser(user);

        System.out.println("Found " + orders.size() + " orders for user: " + userId);

        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Method to update order status after payment
    public OrderDto updateOrderStatus(Long orderId, String status) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        Orders updatedOrder = orderRepository.save(order);

        return convertToDTO(updatedOrder);
    }

    // Method to get order by ID
    public OrderDto getOrderById(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return convertToDTO(order);
    }
}
// [file content end]