package rw.ac.auca.employee.service;

import rw.ac.auca.employee.domain.Employee;

import java.util.List;

public interface EmployeeService {

    Employee registerEmployee(Employee theEmployee);
    Employee updateEmployee(Employee theEmployee);
    void deleteEmployee(Employee theEmployee);
    Employee findEmployeeById(Employee theEmployee);
    List<Employee> findAllEmployees();
}
