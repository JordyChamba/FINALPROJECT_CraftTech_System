/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (BACKEND)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Notifiy.Observable;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Notifiy.Observer;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.OrderConsumer;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.OrderStatus;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Repository.OrderConsumerRepository;

import java.util.List;

@Service
public class OrderConsumerService {

    @Autowired
    private OrderConsumerRepository orderConsumerRepository;
    @Autowired
    private Observable observable;

    public List<OrderConsumer> findAll() {
        return orderConsumerRepository.findAll();
    }

    public OrderConsumer findById(Long id) {
        return orderConsumerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderConsumer not found with id: " + id));
    }

    public OrderConsumer save(OrderConsumer orderConsumer) {
        return orderConsumerRepository.save(orderConsumer);
    }

    public void delete(Long id) {
        if (!orderConsumerRepository.existsById(id)) {
            throw new EntityNotFoundException("OrderConsumer not found with id: " + id);
        }
        orderConsumerRepository.deleteById(id);
    }

    public OrderConsumer updateOrderStatus(Long id, OrderStatus newStatus) {
        OrderConsumer orderConsumer = orderConsumerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderConsumer.setStatus(newStatus);
        return orderConsumerRepository.save(orderConsumer);
    }

    public List<OrderConsumer> findByUserId(Long userId) {
        return orderConsumerRepository.findByUserId(userId);
    }

    public void addObserver(Observer observer) {
        observable.addObserver(observer);
    }

    public void removeObserver(Observer observer) {
        observable.removeObserver(observer);
    }
}
