package com.scc.dao;

import com.scc.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public boolean addAppointment(
            int userId,
            String petName,
            String ownerName,
            String veterinarian,
            String appointmentDate,
            String service
    ) {
        String sql = """
                INSERT INTO appointments
                (pet_name, owner_name, veterinarian,
                 appointment_date, service, status)
                VALUES (?, ?, ?, ?, ?, 'PENDING')
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, petName);
            pstmt.setString(2, ownerName);
            pstmt.setString(3, veterinarian);
            pstmt.setString(4, appointmentDate);
            pstmt.setString(5, service);

            boolean added = pstmt.executeUpdate() > 0;

            if (added) {
                activityLogDao.logAction(
                        userId,
                        "APPOINTMENT_CREATED",
                        "Created appointment for pet: " + petName
                );
            }

            return added;

        } catch (SQLException e) {
            System.err.println("Error adding appointment: " + e.getMessage());
            return false;
        }
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();

        String sql = """
                SELECT id, pet_name, owner_name, veterinarian,
                       appointment_date, service, status
                FROM appointments
                ORDER BY id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getString("pet_name"),
                        rs.getString("owner_name"),
                        rs.getString("veterinarian"),
                        rs.getString("appointment_date"),
                        rs.getString("service"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving appointments: " + e.getMessage());
        }

        return appointments;
    }

    public boolean updateAppointmentStatus(
            int userId,
            int appointmentId,
            String status
    ) {
        String sql = """
                UPDATE appointments
                SET status = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);

            boolean updated = pstmt.executeUpdate() > 0;

            if (updated) {
                activityLogDao.logAction(
                        userId,
                        "APPOINTMENT_UPDATED",
                        "Updated status of appointment ID: "
                                + appointmentId + " to " + status
                );
            }

            return updated;

        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            return false;
        }
    }
}
