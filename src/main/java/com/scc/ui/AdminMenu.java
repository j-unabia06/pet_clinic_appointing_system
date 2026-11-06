package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.model.User;
import com.scc.model.ActivityLog;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final UserDao userDao;
    private final ActivityLogDao activityLogDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.activityLogDao = new ActivityLogDao();
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
            System.out.println("4. Update User Role and Status");
            System.out.println("5. View System Audit Logs");
            System.out.println("6. Logout");
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
                    approveUser(admin);
                    break;

                case "4":
                    updateUser(admin);
                    break;

                case "5":
                    auditLogMenu();
                    break;

                case "6":
                    loggedIn = false;
                    System.out.println("Logging out...");
                    break;

                case "0":
                    loggedIn = false;
                    System.out.println("Exiting Admin Menu...");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
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

    private void approveUser(User admin) {

        List<User> pendingUsers = userDao.getPendingUsers();

        displayUserTable(pendingUsers, "PENDING REGISTRATIONS");

        if (pendingUsers.isEmpty()) {
            return;
        }

        System.out.println("Enter 0 to cancel approval.");

        int userId = readIntInput("Enter Target User ID: ");

        if (userId == 0) {
            System.out.println("Approval cancelled.");
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

        boolean approved = userDao.approveUser(
                userId,
                admin.id()
        );

        if (approved) {
            System.out.println(
                    "SUCCESS! User ID " + userId + " has been approved.");
        } else {
            System.out.println(
                    "Approval failed. Please check the database.");
        }
    }

    private void updateUser(User currentAdmin) {

        List<User> users = userDao.getAllUsers();

        displayUserTable(users, "UPDATE USER ROLE AND STATUS");

        if (users.isEmpty()) {
            return;
        }

        System.out.println("Enter 0 at any step to cancel the update.");

        int targetUserId = readIntInput("Enter Target User ID: ");

        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        User targetUser = null;

        for (User user : users) {
            if (user.id() == targetUserId) {
                targetUser = user;
                break;
            }
        }

        if (targetUser == null) {
            System.out.println("User ID not found.");
            return;
        }

        if (targetUserId == currentAdmin.id()) {
            System.out.println(
                    "WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        System.out.println("\nSelected User: " + targetUser.username());
        System.out.println("Current Role: " + targetUser.roles());
        System.out.println("Current Status: " + targetUser.status());

        String newRole = readValidRole();

        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        boolean updated = userDao.updateUserRoleAndStatus(
                targetUserId,
                newRole,
                newStatus,
                currentAdmin.id()
        );

        if (updated) {
            System.out.println("\n================================");
            System.out.println("USER UPDATED SUCCESSFULLY!");
            System.out.println("User ID: " + targetUserId);
            System.out.println("New Role: " + newRole);
            System.out.println("New Status: " + newStatus);
            System.out.println("================================");
        } else {
            System.out.println(
                    "Update failed. Please check the database.");
        }
    }

    private void auditLogMenu() {

        boolean viewing = true;

        while (viewing) {
            System.out.println("\n================================");
            System.out.println("       SYSTEM AUDIT LOGS");
            System.out.println("================================");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("0. Back");
            System.out.print("Select: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    displayAuditLogs(activityLogDao.getAllLogs());
                    break;

                case "2":
                    int userId = readIntInput(
                            "Enter User ID (0 to cancel): ");

                    if (userId != 0) {
                        displayAuditLogs(
                                activityLogDao.getLogsByUserId(userId));
                    }
                    break;

                case "0":
                    viewing = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void displayAuditLogs(List<ActivityLog> logs) {

        System.out.println("\n================ SYSTEM AUDIT LOGS ================");

        System.out.printf(
                "%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID",
                "User ID",
                "Action Type",
                "Description",
                "Timestamp"
        );

        System.out.println("----------------------------------------------------------------------------------------------------------");

        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        for (ActivityLog log : logs) {
            System.out.printf(
                    "%-5d | %-8d | %-18s | %-35s | %-20s%n",
                    log.id(),
                    log.userId(),
                    log.actionType(),
                    log.description(),
                    log.timestamp()
            );
        }

        System.out.println("----------------------------------------------------------------------------------------------------------");
    }

    private int readIntInput(String prompt) {

        while (true) {
            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input! Please enter a valid number.");
            }
        }
    }

    private String readValidRole() {

        while (true) {
            System.out.println("\nAvailable Roles:");
            System.out.println("ADMIN");
            System.out.println("RECEPTIONIST");
            System.out.println("VETERINARIAN");
            System.out.print("Enter New Role (or 0 to cancel): ");

            String role = scanner.nextLine().trim().toUpperCase();

            if (role.equals("0")) {
                return null;
            }

            if (role.equals("ADMIN")
                    || role.equals("RECEPTIONIST")
                    || role.equals("VETERINARIAN")) {
                return role;
            }

            System.out.println(
                    "Invalid role! Please enter ADMIN, RECEPTIONIST, or VETERINARIAN.");
        }
    }

    private String readValidStatus() {

        while (true) {
            System.out.println("\nAvailable Statuses:");
            System.out.println("APPROVED");
            System.out.println("PENDING");
            System.out.print("Enter New Status (or 0 to cancel): ");

            String status = scanner.nextLine().trim().toUpperCase();

            if (status.equals("0")) {
                return null;
            }

            if (status.equals("APPROVED")
                    || status.equals("PENDING")) {
                return status;
            }

            System.out.println(
                    "Invalid status! Please enter APPROVED or PENDING.");
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
