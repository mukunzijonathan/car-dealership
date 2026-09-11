package rw.ac.auca.dealership.model;

import javax.persistence.*;
import javax.validation.constraints.*;

@MappedSuperclass
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotBlank(message = "First name is required")
    protected String firstName;

    @NotBlank(message = "Last name is required")
    protected String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    @Column(unique = true)
    protected String email;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Phone must be 9-15 digits")
    protected String phoneNumber;

    public String getFullName() { return firstName + " " + lastName; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
