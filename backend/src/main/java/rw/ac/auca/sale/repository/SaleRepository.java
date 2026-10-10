package rw.ac.auca.sale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.ac.auca.sale.domain.Sale;
import rw.ac.auca.sale.domain.SaleStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {
    Optional<Sale> findByCarId(UUID carId);
    boolean existsByCarId(UUID carId);
    boolean existsByCustomerId(UUID customerId);
    boolean existsByEmployeeId(UUID employeeId);
    List<Sale> findByCustomerId(UUID customerId);
    List<Sale> findByStatus(SaleStatus status);
}
