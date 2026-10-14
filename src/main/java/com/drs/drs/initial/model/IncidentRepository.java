
package com.drs.drs.initial.model;

import com.drs.drs.initial.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class IncidentRepository {

    private static final ObservableList<Incident> incidents =
            FXCollections.observableArrayList();

    static {
        loadIncidents();
    }

    /**
     * Load all incidents from MySQL.
     */
    public static void loadIncidents() {

        String sql = """
            SELECT * FROM incidents
            ORDER BY incident_id
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            ObservableList<Incident> loaded =
                    FXCollections.observableArrayList();

            while (resultSet.next()) {

                Timestamp reported =
                        resultSet.getTimestamp("reported_at");

                Timestamp resolved =
                        resultSet.getTimestamp("resolved_at");

                LocalDateTime reportedAt =
                        reported == null
                        ? LocalDateTime.now()
                        : reported.toLocalDateTime();

                Incident incident = new Incident(
                        resultSet.getString("incident_id"),
                        resultSet.getString("disaster_type"),
                        resultSet.getString("location"),
                        resultSet.getString("description"),
                        resultSet.getString("priority"),
                        resultSet.getString("status"),
                        reportedAt,
                        resultSet.getInt("injured_count")
                );

                if (resolved != null) {
                    // Incident has no setResolvedAt() method.
                    // Resolution timestamp remains in MySQL.
                }

                loaded.add(incident);
            }

            incidents.setAll(loaded);

            System.out.println(
                    "Loaded " + incidents.size()
                    + " incidents from MySQL."
            );

        } catch (SQLException e) {
            System.err.println(
                    "Failed to load incidents from MySQL."
            );
            throw new IllegalStateException(
                    "Database loading failed.", e
            );
        }
    }

    /**
     * Return the shared JavaFX list.
     */
    public static ObservableList<Incident> getIncidents() {
        return incidents;
    }

    /**
     * Save a new incident to MySQL.
     */
    public static void addIncident(Incident incident) {

        if (incident == null) {
            return;
        }

        String sql = """
            INSERT INTO incidents (
                incident_id,
                disaster_type,
                location,
                description,
                priority,
                status,
                reported_at,
                resolved_at,
                injured_count
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, incident.getIncidentId());
            statement.setString(2, incident.getDisasterType());
            statement.setString(3, incident.getLocation());
            statement.setString(4, incident.getDescription());
            statement.setString(5, incident.getPriority());
            statement.setString(6, incident.getStatus());

            statement.setTimestamp(
                    7,
                    incident.getReportedAt() == null
                    ? null
                    : Timestamp.valueOf(incident.getReportedAt())
            );

            statement.setTimestamp(
                    8,
                    incident.getResolvedAt() == null
                    ? null
                    : Timestamp.valueOf(incident.getResolvedAt())
            );

            statement.setInt(9, incident.getInjuredCount());

            statement.executeUpdate();

            incidents.add(incident);

            System.out.println(
                    "Incident saved: "
                    + incident.getIncidentId()
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to save incident: "
                    + incident.getIncidentId(), e
            );
        }
    }

    /**
     * Generate a unique incident ID.
     */
    public static String generateIncidentId() {

        int maxId = 0;

        for (Incident incident : incidents) {

            String id = incident.getIncidentId();

            if (id != null && id.matches("INC-\\d+")) {

                int number = Integer.parseInt(
                        id.substring(4)
                );

                maxId = Math.max(maxId, number);
            }
        }

        return String.format("INC-%03d", maxId + 1);
    }
}
