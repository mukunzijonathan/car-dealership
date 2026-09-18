package rw.ac.auca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.car.domain.Car;
import rw.ac.auca.car.service.CarService;

import java.util.List;
import java.util.UUID;

@RestController
@Controller
@RequestMapping("/car")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/search-all")
    @ResponseStatus(HttpStatus.OK)
    public List<Car> findAllRecords() {
        return carService.findAllCars();
    }

    @GetMapping("/search-by-id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Car findById(@PathVariable UUID id) {
        Car probe = new Car();
        probe.setId(id);
        return carService.findCarById(probe);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public Car createCar(@RequestBody Car theCar) {
        return carService.registerCar(theCar);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public Car updateCar(@RequestBody Car theCar) {
        return carService.updateCar(theCar);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteCar(@PathVariable UUID id) {
        Car probe = new Car();
        probe.setId(id);
        carService.deleteCar(probe);
        return "Deleted";
    }
}
