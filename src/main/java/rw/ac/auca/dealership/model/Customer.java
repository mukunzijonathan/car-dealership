package rw.ac.auca.dealership.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
public class Customer extends Person {

    @Column(name = "registered_on")
    private LocalDate registeredOn;

    @PrePersist
    protected void onRegister() {
        if (registeredOn == null) registeredOn = LocalDate.now();
    }

    public LocalDate getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(LocalDate registeredOn) { this.registeredOn = registeredOn; }
}
