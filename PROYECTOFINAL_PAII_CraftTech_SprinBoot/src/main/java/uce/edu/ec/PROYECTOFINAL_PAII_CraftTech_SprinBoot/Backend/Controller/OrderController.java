/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (BACKEND)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Service.OrderConsumerService;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Service.UserService;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.OrderConsumer;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.OrderStatus;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.Product;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Service.ProductService;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Notifiy.Observable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderConsumerService orderConsumerService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private Observable observable;

    public OrderController(OrderConsumerService orderConsumerService, Observable observable) {
        this.orderConsumerService = orderConsumerService;
        this.observable = observable;
    }

    @GetMapping
    public ResponseEntity<List<OrderConsumer>> getOrders() {
        List<OrderConsumer> orders = orderConsumerService.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderConsumer> getOrderById(@PathVariable Long id) {
        OrderConsumer order = orderConsumerService.findById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<OrderConsumer> createOrder(@Validated @RequestBody OrderConsumer order) {
        if (order == null || order.getUserId() == null || order.getProductName() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        if (userService.findById(order.getUserId()) == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Product product = productService.findByName(order.getProductName());
        if (product == null) {
            return ResponseEntity.badRequest().body(null);
        }

        OrderConsumer newOrder = new OrderConsumer();
        newOrder.setUserId(order.getUserId());
        newOrder.setProductName(order.getProductName());
        newOrder.setOrderDate(order.getOrderDate() != null ? order.getOrderDate() : new Date());
        newOrder.setStatus(order.getStatus() != null ? order.getStatus() : OrderStatus.IN_PROGRESS);

        OrderConsumer savedOrder = orderConsumerService.save(newOrder);

        List<OrderConsumer> orders = new ArrayList<>();
        orders.add(savedOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @PutMapping("/{id}/updateStatus")
    public ResponseEntity<OrderConsumer> updateOrderStatus(@PathVariable Long id, @RequestBody String newStatus) {
        OrderConsumer order = orderConsumerService.findById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase());
            order.setStatus(status);
            orderConsumerService.save(order);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<OrderConsumer>> getNotificationsByUserId(@PathVariable Long userId) {
        List<OrderConsumer> orders = orderConsumerService.findByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

}
