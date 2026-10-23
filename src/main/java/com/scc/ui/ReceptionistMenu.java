
package com.scc.ui;

import com.scc.dao.AppointmentDao;
import com.scc.model.Appointment;

import java.util.List;
import java.util.Scanner;

public class ReceptionistMenu {

    private final AppointmentDao appointmentDao;
    private final Scanner sc;

    public ReceptionistMenu(Scanner scanner) {
        this.appointmentDao = new AppointmentDao();
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
            System.out.println("3. Logout");
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

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        System.out.println("\n--- APPOINTMENT LIST ---");

        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
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
}
