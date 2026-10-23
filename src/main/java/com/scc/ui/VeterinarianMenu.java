
package com.scc.ui;

import com.scc.dao.AppointmentDao;
import com.scc.model.Appointment;

import java.util.List;
import java.util.Scanner;

public class VeterinarianMenu {

    private final AppointmentDao appointmentDao;
    private final Scanner sc;

    public VeterinarianMenu(Scanner scanner) {
        this.appointmentDao = new AppointmentDao();
        this.sc = scanner;
    }

    public void display() {
        boolean running = true;

        while (running) {
            System.out.println("\n====================================");
            System.out.println("       VETERINARIAN MENU");
            System.out.println("====================================");
            System.out.println("1. View Appointments");
            System.out.println("2. Update Appointment Status");
            System.out.println("3. Logout");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAppointments();
                    break;
                case "2":
                    updateAppointmentStatus();
                    break;
                case "3":
                    running = false;
                    System.out.println("Veterinarian logged out.");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void viewAppointments() {
        List<Appointment> appointments =
                appointmentDao.getAllAppointments();

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        System.out.println("\n--- APPOINTMENT LIST ---");

        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }

    private void updateAppointmentStatus() {
        viewAppointments();

        System.out.print("\nEnter Appointment ID (0 to cancel): ");
        String input = sc.nextLine().trim();

        try {
            int appointmentId = Integer.parseInt(input);

            if (appointmentId == 0) {
                System.out.println("Update cancelled.");
                return;
            }

            System.out.println("Select new status:");
            System.out.println("1. APPROVED");
            System.out.println("2. COMPLETED");
            System.out.println("3. CANCELLED");
            System.out.print("Choose status: ");

            String choice = sc.nextLine().trim();
            String status;

            switch (choice) {
                case "1":
                    status = "APPROVED";
                    break;
                case "2":
                    status = "COMPLETED";
                    break;
                case "3":
                    status = "CANCELLED";
                    break;
                default:
                    System.out.println("Invalid status selection.");
                    return;
            }

            boolean updated = appointmentDao.updateAppointmentStatus(
                    appointmentId, status);

            if (updated) {
                System.out.println("Appointment status updated successfully.");
            } else {
                System.out.println(
                        "Appointment not found or update failed.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid numeric ID.");
        }
    }
}
