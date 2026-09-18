package rw.ac.auca.car.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rw.ac.auca.car.domain.Car;
import rw.ac.auca.car.domain.CarStatus;
import rw.ac.auca.car.repository.CarRepository;

import java.time.Year;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Override
    public Car registerCar(Car theCar) {
        validateCar(theCar, true);
        if (theCar.getStatus() == null) {
            theCar.setStatus(CarStatus.AVAILABLE);
        }
        theCar.setVin(theCar.getVin().trim().toUpperCase());
        return carRepository.save(theCar);
    }

    @Override
    public Car updateCar(Car theCar) {
        Car found = findCarById(theCar);
        validateCar(theCar, false);
        if (found.getStatus() == CarStatus.SOLD && theCar.getStatus() != CarStatus.SOLD) {
            throw new RuntimeException("A sold car cannot be moved back to " + theCar.getStatus());
        }
        found.setVin(theCar.getVin().trim().toUpperCase());
        found.setBrand(theCar.getBrand().trim());
        found.setModel(theCar.getModel().trim());
        found.setManufactureYear(theCar.getManufactureYear());
        found.setPrice(theCar.getPrice());
        found.setStatus(theCar.getStatus());
        return carRepository.save(found);
    }

    @Override
    public void deleteCar(Car theCar) {
        Car found = findCarById(theCar);
        if (found.getStatus() == CarStatus.SOLD) {
            throw new RuntimeException("A sold car cannot be deleted; it is part of the sales history");
        }
        carRepository.delete(found);
    }

    @Override
    public Car findCarById(Car theCar) {
        return carRepository.findById(theCar.getId())
                .orElseThrow(() -> new RuntimeException("Object not found!"));
    }

    @Override
    public List<Car> findAllCars() {
        return carRepository.findAll();
    }

    private void validateCar(Car theCar, boolean creating) {
        if (theCar.getVin() == null || theCar.getVin().trim().length() != 17) {
            throw new RuntimeException("VIN must be 17 characters");
        }
        if (theCar.getBrand() == null || theCar.getBrand().isBlank()) {
            throw new RuntimeException("Brand is required");
        }
        if (theCar.getModel() == null || theCar.getModel().isBlank()) {
            throw new RuntimeException("Model is required");
        }
        if (theCar.getPrice() == null || theCar.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than zero");
        }
        int maxYear = Year.now().getValue() + 1;
        if (theCar.getManufactureYear() == null
                || theCar.getManufactureYear() < 1980
                || theCar.getManufactureYear() > maxYear) {
            throw new RuntimeException("Year must be between 1980 and " + maxYear);
        }
        carRepository.findByVinIgnoreCase(theCar.getVin().trim()).ifPresent(existing -> {
            if (creating || !existing.getId().equals(theCar.getId())) {
                throw new RuntimeException("A car with this VIN already exists");
            }
        });
    }
}
