
package com.drs.drs.initial.database;

import com.drs.drs.initial.model.Incident;
import com.drs.drs.initial.model.IncidentRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class IncidentDataMigration {

    public static void migrateIncidents() {

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
            ON DUPLICATE KEY UPDATE
                incident_id = incident_id
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            int inserted = 0;

            for (Incident incident :
                    IncidentRepository.getIncidents()) {

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

                int affected = statement.executeUpdate();

                if (affected == 1) {
                    inserted++;
                }
            }

            System.out.println(
                "Incident migration completed. New records: "
                + inserted
            );

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException(
                "Incident migration failed.", e
            );
        }
    }

    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();
        migrateIncidents();
    }
}
