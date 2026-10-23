
package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void display(User admin) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n================================");
            System.out.println("       ADMIN DASHBOARD");
            System.out.println("================================");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.println("0. Exit");
            System.out.print("Select: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;

                case "2":
                    viewPendingUsers();
                    break;

                case "3":
                    approveUser();
                    break;

                case "4":
                    loggedIn = false;
                    System.out.println("Logging out...");
                    break;

                case "0":
                    loggedIn = false;
                    System.out.println("Exiting Admin Menu...");
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please try again.");
            }
        }
    }


    private void viewAllUsers() {
        List<User> users = userDao.getAllUsers();
        displayUserTable(users, "ALL SYSTEM USERS");
    }


    private void viewPendingUsers() {
        List<User> users = userDao.getPendingUsers();
        displayUserTable(users, "PENDING REGISTRATIONS");
    }


    private void approveUser() {
        List<User> pendingUsers = userDao.getPendingUsers();

        displayUserTable(
                pendingUsers,
                "PENDING REGISTRATIONS"
        );

        if (pendingUsers.isEmpty()) {
            return;
        }

        System.out.println("Enter 0 to cancel approval.");
        System.out.print("Enter Target User ID: ");

        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("Approval cancelled.");
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(
                    "Invalid User ID. Please enter a number.");
            return;
        }

        boolean found = false;

        for (User user : pendingUsers) {
            if (user.id() == userId) {
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println(
                    "User ID not found in pending registrations.");
            return;
        }

        boolean approved = userDao.approveUser(userId);

        if (approved) {
            System.out.println(
                    "SUCCESS! User ID " + userId
                            + " has been approved.");
        } else {
            System.out.println(
                    "Approval failed. Please check the database.");
        }
    }


    private void displayUserTable(
            List<User> users, String title) {

        System.out.println("\n========== " + title + " ==========");

        String border =
                "+-------+-----------------+--------------+------------+";

        System.out.println(border);

        System.out.printf(
                "| %-5s | %-15s | %-12s | %-10s |%n",
                "ID",
                "Username",
                "Role",
                "Status"
        );

        System.out.println(border);

        if (users.isEmpty()) {
            System.out.printf(
                    "| %-49s |%n",
                    "No records found."
            );
        } else {
            for (User user : users) {
                System.out.printf(
                        "| %-5s | %-15s | %-12s | %-10s |%n",
                        user.id(),
                        user.username(),
                        user.roles(),
                        user.status()
                );
            }
        }

        System.out.println(border);
    }
}
