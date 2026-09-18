package rw.ac.auca.customer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rw.ac.auca.base.Person;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends Person {

    @Column(name = "registered_on", nullable = false)
    private LocalDate registeredOn;

    @Column(name = "credit_limit", nullable = false)
    private Double creditLimit = 0.0;

    @PrePersist
    public void onRegister() {
        if (registeredOn == null) {
            registeredOn = LocalDate.now();
        }
        if (getEmail() != null) {
            setEmail(getEmail().trim().toLowerCase());
        }
    }
}
