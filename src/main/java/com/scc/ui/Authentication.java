
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner sc;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.sc = scanner;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n====================================");
            System.out.println("   PET CLINIC APPOINTMENT SYSTEM");
            System.out.println("====================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Select an Option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;

                case "2":
                    handleRegistration();
                    break;

                case "3":
                    System.out.println(
                            "Thank you for using Pet Clinic Appointment System!"
                    );
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleRegistration() {
        System.out.print("Enter new Username: ");
        String newUser = sc.nextLine().trim();

        if (newUser.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        System.out.print("Enter Password: ");
        String newPass = sc.nextLine();

        if (newPass.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        System.out.println(
                "Select Role: [1] Admin [2] Veterinarian [3] Receptionist"
        );
        System.out.print("Select Role: ");

        String roleChoice = sc.nextLine().trim();
        String role;

        switch (roleChoice) {
            case "1":
                role = "ADMIN";
                break;

            case "2":
                role = "VETERINARIAN";
                break;

            case "3":
                role = "RECEPTIONIST";
                break;

            default:
                System.out.println("Invalid role selection!");
                return;
        }

        userDao.registerUser(newUser, newPass, role);
    }

    private void handleLogin() {
        System.out.print("Enter Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            return;
        }

        System.out.println(
                "Welcome " + user.username()
                        + " | ROLE: " + user.roles()
        );

        switch (user.roles().toUpperCase()) {
            case "ADMIN":
                new AdminMenu(userDao, sc).display(user);
                break;

            case "VETERINARIAN":
                new VeterinarianMenu(sc).display();
                break;

            case "RECEPTIONIST":
                new ReceptionistMenu(sc).display();
                break;

            default:
                System.out.println("Account role not recognized.");
        }
    }
}
