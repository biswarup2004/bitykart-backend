// [file name]: OrderController.java
// [file content begin]
package com.bity.bitykart.controller;

import com.bity.bitykart.dto.OrderDto;
import com.bity.bitykart.model.OrderRequest;
import com.bity.bitykart.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{userId}")
    public OrderDto placeOrder(@PathVariable Long userId, @RequestBody OrderRequest orderRequest){
        System.out.println("=== PLACE ORDER ===");
        System.out.println("User ID: " + userId);
        System.out.println("Order Request: " + orderRequest.getProductQuantities());
        System.out.println("Total Amount: " + orderRequest.getTotalAmount());

        // Debug authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + auth);
        System.out.println("Principal: " + auth.getPrincipal());
        System.out.println("Authorities: " + auth.getAuthorities());

        return orderService.placeOrder(userId, orderRequest.getProductQuantities(),
                orderRequest.getTotalAmount(), orderRequest.getPaymentMethod());
    }

    @GetMapping("/all-orders")
    public List<OrderDto> getAllOrders(){
        System.out.println("=== GET ALL ORDERS ===");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + auth);
        return orderService.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public List<OrderDto> getOrderByUser(@PathVariable Long userId){
        System.out.println("=== GET ORDERS BY USER ===");
        System.out.println("Requested User ID: " + userId);

        // Debug authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + auth);
        System.out.println("Principal: " + (auth != null ? auth.getPrincipal() : "null"));
        System.out.println("Is Authenticated: " + (auth != null ? auth.isAuthenticated() : "false"));

        if (auth == null || !auth.isAuthenticated()) {
            System.out.println("USER NOT AUTHENTICATED - 403 WILL BE RETURNED");
        }

        return orderService.getOrderByUser(userId);
    }

    @PutMapping("/{orderId}/status")
    public OrderDto updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    // Add a public test endpoint
    @GetMapping("/test")
    public String test() {
        return "Orders endpoint is working!";
    }

    // Add debug endpoint
    @GetMapping("/debug/{userId}")
    public String debugUser(@PathVariable Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Debug - User ID: " + userId +
                ", Authenticated: " + (auth != null ? auth.isAuthenticated() : "false") +
                ", Principal: " + (auth != null ? auth.getPrincipal() : "null");
    }

    // Add this to OrderController.java
    @PostMapping("/test-create")
    public String testCreateOrder() {
        try {
            System.out.println("=== TEST ORDER CREATION ===");

            // Create a simple test order
            Map<Long, Integer> testProducts = new HashMap<>();
            testProducts.put(1L, 2); // Product ID 1, quantity 2
            testProducts.put(2L, 1); // Product ID 2, quantity 1

            OrderDto testOrder = orderService.placeOrder(1L, testProducts, 500.0, "credit-card");

            return "Test order created successfully: " + testOrder.getId();
        } catch (Exception e) {
            return "Test order failed: " + e.getMessage();
        }
    }
    @GetMapping("/user/{userId}/debug")
    public ResponseEntity<?> debugUserOrders(@PathVariable Long userId) {
        try {
            System.out.println("=== DEBUG ORDERS ENDPOINT ===");
            System.out.println("User ID: " + userId);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authenticated: " + (auth != null && auth.isAuthenticated()));
            System.out.println("Principal: " + (auth != null ? auth.getPrincipal() : "null"));

            List<OrderDto> orders = orderService.getOrderByUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", auth != null && auth.isAuthenticated());
            response.put("userId", userId);
            response.put("orderCount", orders.size());
            response.put("orders", orders);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
// [file content end]