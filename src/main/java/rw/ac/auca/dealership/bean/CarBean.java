package rw.ac.auca.dealership.bean;

import rw.ac.auca.dealership.dao.CarDAO;
import rw.ac.auca.dealership.model.Car;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "carBean")
@ViewScoped
public class CarBean implements Serializable {
    private CarDAO dao = new CarDAO();
    private Car car = new Car();
    private List<Car> cars;

    public String save() {
        dao.save(car);
        car = new Car();
        cars = null;
        return null;
    }

    public String edit(Car c) {
        this.car = c;
        return null;
    }

    public String delete(Long id) {
        dao.delete(id);
        cars = null;
        return null;
    }

    public List<Car> getCars() {
        if (cars == null) cars = dao.findAll();
        return cars;
    }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
}