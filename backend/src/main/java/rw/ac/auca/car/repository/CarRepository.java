package rw.ac.auca.car.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.ac.auca.car.domain.Car;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
    Optional<Car> findByVinIgnoreCase(String vin);
    boolean existsByVinIgnoreCase(String vin);
}
