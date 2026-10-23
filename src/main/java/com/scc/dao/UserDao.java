
package com.scc.dao;

import com.scc.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public UserDao() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            defaultAdminGenerator(conn);
        } catch (SQLException e) {
            System.err.println("Error initializing admin: " + e.getMessage());
        }
    }


    public void defaultAdminGenerator(Connection conn) {
        String checkSql =
                "SELECT COUNT(*) FROM users WHERE username = ?";

        String insertSql =
                "INSERT INTO users (username, password, roles, status) " +
                        "VALUES (?, ?, ?, ?)";

        try (PreparedStatement checkStmt =
                     conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, "admin");

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {

                    try (PreparedStatement insertStmt =
                                 conn.prepareStatement(insertSql)) {

                        insertStmt.setString(1, "admin");
                        insertStmt.setString(2, "admin123");
                        insertStmt.setString(3, "ADMIN");
                        insertStmt.setString(4, "APPROVED");

                        insertStmt.executeUpdate();

                        System.out.println(
                                " -> Default Admin created successfully.");
                    }

                } else {
                    System.out.println(
                            " -> Admin username already exists.");
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error creating default admin: " + e.getMessage());
        }
    }


    public boolean registerUser(
            String username, String password, String role) {

        String sql =
                "INSERT INTO users (username, password, roles, status) " +
                        "VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());

            pstmt.executeUpdate();

            System.out.println(
                    "SUCCESSFUL REGISTRATION. Waiting for approval.");

            return true;

        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }


    public User login(String username, String password) {

        String sql =
                "SELECT id, username, password, roles, status " +
                        "FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    String status = rs.getString("status");

                    if (!"APPROVED".equalsIgnoreCase(status)) {
                        System.out.println(
                                "Account is pending approval.");
                        return null;
                    }

                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            status
                    );
                }

                System.out.println("Invalid Username or Password.");
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }

        return null;
    }


    public List<User> getAllUsers() {
        List<User> usersList = new ArrayList<>();

        String sql =
                "SELECT id, username, password, roles, status FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                usersList.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }

        return usersList;
    }


    public List<User> getPendingUsers() {
        List<User> usersList = new ArrayList<>();

        String sql =
                "SELECT id, username, password, roles, status " +
                        "FROM users WHERE status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "PENDING");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    usersList.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error fetching pending users: " + e.getMessage());
        }

        return usersList;
    }


    public boolean approveUser(int userId) {

        String sql =
                "UPDATE users SET status = 'APPROVED' " +
                        "WHERE id = ? AND status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println(
                    "Error approving user: " + e.getMessage());
        }

        return false;
    }
}
