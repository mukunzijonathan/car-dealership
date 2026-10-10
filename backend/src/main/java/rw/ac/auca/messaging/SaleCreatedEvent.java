package rw.ac.auca.messaging;

import java.util.UUID;

public record SaleCreatedEvent(
        UUID saleId,
        UUID carId,
        String vin,
        String customerEmail,
        String customerPhone,
        String employeeEmail,
        Double amount
) {
}
