/**
 * @author KevinPozo
 * @author BrayanLoya
 * @author JordyChamba
 * Title: CraftTech Fabrication System (BACKEND)
 * */
package uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Notifiy;

import org.springframework.stereotype.Component;
import uce.edu.ec.PROYECTOFINAL_PAII_CraftTech_SprinBoot.Backend.Model.OrderConsumer;

import java.util.ArrayList;
import java.util.List;

@Component
public class Observable {

    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

}
