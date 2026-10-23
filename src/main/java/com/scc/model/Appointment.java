package com.scc.model;

public record Appointment(
        int id,
        String petName,
        String ownerName,
        String veterinarian,
        String appointmentDate,
        String service,
        String status
) {
    @Override
    public String toString() {
        return String.format(
                "[%d] %s | Owner: %s | Vet: %s | Date: %s | Service: %s | Status: %s",
                id, petName, ownerName, veterinarian,
                appointmentDate, service, status
        );
    }
}
