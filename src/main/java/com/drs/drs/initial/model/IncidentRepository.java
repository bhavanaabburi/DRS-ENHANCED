
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
        // JavaFX -> TCP server -> MySQL. Do not clear the displayed
        // list until the entire server response has been validated.
        try (java.net.Socket socket = com.drs.drs.initial.server.TLSConnection.open()) {
            socket.setSoTimeout(5000);
            try (java.io.PrintWriter out = new java.io.PrintWriter(
                    new java.io.OutputStreamWriter(socket.getOutputStream(),
                            java.nio.charset.StandardCharsets.UTF_8), true);
                 java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(socket.getInputStream(),
                            java.nio.charset.StandardCharsets.UTF_8))) {

                out.println(com.drs.drs.initial.server.DRSSession.wrap("GET_INCIDENTS_V2"));
                String header = in.readLine();
                if (header == null || !header.matches("OK [0-9]+")) {
                    throw new java.io.IOException("Invalid server response: " + header);
                }
                int count = Integer.parseInt(header.substring(3));
                if (count > 100000) {
                    throw new java.io.IOException("Unexpected incident count: " + count);
                }
                ObservableList<Incident> loaded = FXCollections.observableArrayList();
                for (int row = 0; row < count; row++) {
                    String line = in.readLine();
                    if (line == null) throw new java.io.EOFException("Incomplete incident list");
                    String[] encoded = line.split("\t", -1);
                    if (encoded.length != 8) {
                        throw new java.io.IOException("Malformed incident record " + row);
                    }
                    String[] fields = new String[8];
                    for (int i = 0; i < 8; i++) {
                        fields[i] = new String(
                                java.util.Base64.getDecoder().decode(encoded[i]),
                                java.nio.charset.StandardCharsets.UTF_8);
                    }
                    loaded.add(new Incident(
                            fields[0], fields[1], fields[2], fields[3],
                            fields[4], fields[5],
                            java.time.LocalDateTime.parse(fields[6].replace(' ', 'T')),
                            Integer.parseInt(fields[7])));
                }
                incidents.setAll(loaded);
                System.out.println("Loaded " + incidents.size()
                        + " incidents through DRS server.");
            }
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot load incidents through DRS server. Start DRSServer first.", e);
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
