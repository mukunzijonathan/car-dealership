package rw.ac.auca.car.service;

import rw.ac.auca.car.domain.Car;

import java.util.List;

public interface CarService {

    Car registerCar(Car theCar);
    Car updateCar(Car theCar);
    void deleteCar(Car theCar);
    Car findCarById(Car theCar);
    List<Car> findAllCars();
    List<Car> findAvailableCars();
}
