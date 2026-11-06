package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static boolean initialize() {

        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                );
                """;

        String createAppointmentsTable = """
                CREATE TABLE IF NOT EXISTS appointments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    pet_name TEXT NOT NULL,
                    owner_name TEXT NOT NULL,
                    veterinarian TEXT NOT NULL,
                    appointment_date TEXT NOT NULL,
                    service TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                );
                """;

        String createActivityLogsTable = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action_type TEXT NOT NULL,
                    description TEXT NOT NULL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id)
                        REFERENCES users(id)
                        ON DELETE CASCADE
                );
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createUsersTable);
            stmt.execute(createAppointmentsTable);
            stmt.execute(createActivityLogsTable);

            System.out.println(" -> Database tables verified.");

        } catch (SQLException e) {
            System.err.println(
                    "Database initialization failed: " + e.getMessage()
            );
            return false;
        }

        return true;
    }
}
