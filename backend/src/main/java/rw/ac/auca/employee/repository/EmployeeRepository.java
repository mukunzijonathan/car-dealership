package rw.ac.auca.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.ac.auca.employee.domain.Employee;
import rw.ac.auca.employee.domain.EmployeeRole;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);
    long countByRole(EmployeeRole role);
}
