package rw.ac.auca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.employee.domain.Employee;
import rw.ac.auca.employee.service.EmployeeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/search-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Employee> findAllRecords() {
        return employeeService.findAllEmployees();
    }

    @GetMapping("/search-by-id/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Employee findById(@PathVariable UUID id) {
        Employee probe = new Employee();
        probe.setId(id);
        return employeeService.findEmployeeById(probe);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Employee createEmployee(@RequestBody Employee theEmployee) {
        return employeeService.registerEmployee(theEmployee);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Employee updateEmployee(@RequestBody Employee theEmployee) {
        return employeeService.updateEmployee(theEmployee);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEmployee(@PathVariable UUID id) {
        Employee probe = new Employee();
        probe.setId(id);
        employeeService.deleteEmployee(probe);
        return "Deleted";
    }
}
