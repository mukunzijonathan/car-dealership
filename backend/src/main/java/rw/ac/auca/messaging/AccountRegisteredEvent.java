package rw.ac.auca.messaging;

public record AccountRegisteredEvent(
        String customerEmail,
        String customerPhone,
        String fullName
) {
}
