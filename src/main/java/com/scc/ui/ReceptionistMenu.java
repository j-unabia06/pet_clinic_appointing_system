package com.scc.ui;

import com.scc.dao.AppointmentDao;
import com.scc.model.Appointment;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class ReceptionistMenu {

    private final AppointmentDao appointmentDao;
    private final Scanner sc;
    private final User user;

    public ReceptionistMenu(User user, Scanner scanner) {
        this.appointmentDao = new AppointmentDao();
        this.user = user;
        this.sc = scanner;
    }

    public void display() {
        boolean running = true;

        while (running) {
            System.out.println("\n====================================");
            System.out.println("       RECEPTIONIST MENU");
            System.out.println("====================================");
            System.out.println("1. View Appointments");
            System.out.println("2. Add Appointment");
            System.out.println("3. Cancel / Delete Appointment");
            System.out.println("4. Logout");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAppointments();
                    break;
                case "2":
                    addAppointment();
                    break;
                case "3":
                    cancelOrDeleteAppointment();
                    break;
                case "4":
                    running = false;
                    System.out.println("Receptionist logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void viewAppointments() {
        List<Appointment> appointments =
                appointmentDao.getAllAppointments();

        displayAppointments(appointments);
    }

    private void addAppointment() {
        System.out.println("\n--- ADD APPOINTMENT ---");

        System.out.print("Pet Name: ");
        String petName = sc.nextLine().trim();

        System.out.print("Pet Owner Name: ");
        String ownerName = sc.nextLine().trim();

        System.out.print("Veterinarian Name: ");
        String veterinarian = sc.nextLine().trim();

        System.out.print("Appointment Date and Time: ");
        String appointmentDate = sc.nextLine().trim();

        System.out.print("Clinic Service: ");
        String service = sc.nextLine().trim();

        if (petName.isEmpty()
                || ownerName.isEmpty()
                || veterinarian.isEmpty()
                || appointmentDate.isEmpty()
                || service.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        boolean added = appointmentDao.addAppointment(
                user.id(),
                petName,
                ownerName,
                veterinarian,
                appointmentDate,
                service
        );

        if (added) {
            System.out.println("Appointment added successfully!");
        } else {
            System.out.println("Failed to add appointment.");
        }
    }

    private void cancelOrDeleteAppointment() {
        System.out.println("\n--- CANCEL / DELETE APPOINTMENT ---");

        List<Appointment> appointments =
                appointmentDao.getAllAppointments();

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        displayAppointments(appointments);

        System.out.print("\nSelect ID to delete (0 to cancel): ");
        String idInput = sc.nextLine().trim();

        int appointmentId;
        try {
            appointmentId = Integer.parseInt(idInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid appointment ID.");
            return;
        }

        if (appointmentId == 0) {
            System.out.println("Deletion cancelled.");
            return;
        }

        boolean deleted = appointmentDao.deleteAppointment(
                user.id(),
                appointmentId
        );

        if (deleted) {
            System.out.println("Appointment deleted successfully!");
        } else {
            System.out.println("Failed to delete appointment.");
        }
    }

    private void displayAppointments(List<Appointment> appointments) {

        System.out.println("\n================ APPOINTMENT LIST ================");

        System.out.printf(
                "%-5s | %-15s | %-15s | %-18s | %-20s | %-18s | %-10s%n",
                "ID",
                "Pet Name",
                "Owner",
                "Veterinarian",
                "Appointment Date",
                "Service",
                "Status"
        );

        System.out.println("----------------------------------------------------------------------------------------------------------");

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        for (Appointment appointment : appointments) {
            System.out.printf(
                    "%-5d | %-15s | %-15s | %-18s | %-20s | %-18s | %-10s%n",
                    appointment.id(),
                    appointment.petName(),
                    appointment.ownerName(),
                    appointment.veterinarian(),
                    appointment.appointmentDate(),
                    appointment.service(),
                    appointment.status()
            );
        }

        System.out.println("----------------------------------------------------------------------------------------------------------");
    }
}