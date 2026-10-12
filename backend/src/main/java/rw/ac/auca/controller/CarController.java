package rw.ac.auca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.car.domain.Car;
import rw.ac.auca.car.service.CarService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/car")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/search-available")
    @ResponseStatus(HttpStatus.OK)
    public List<Car> findAvailable() {
        return carService.findAvailableCars();
    }

    @GetMapping("/search-lot")
    @ResponseStatus(HttpStatus.OK)
    public List<Car> findLot() {
        return carService.findAllCars();
    }

    @GetMapping("/search-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT','CUSTOMER')")
    public List<Car> findAllRecords() {
        return carService.findAllCars();
    }

    @GetMapping("/search-by-id/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT','CUSTOMER')")
    public Car findById(@PathVariable UUID id) {
        Car probe = new Car();
        probe.setId(id);
        return carService.findCarById(probe);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT')")
    public Car createCar(@RequestBody Car theCar) {
        return carService.registerCar(theCar);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT')")
    public Car updateCar(@RequestBody Car theCar) {
        return carService.updateCar(theCar);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public String deleteCar(@PathVariable UUID id) {
        Car probe = new Car();
        probe.setId(id);
        carService.deleteCar(probe);
        return "Deleted";
    }
}
