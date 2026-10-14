package com.drs.drs.initial.model;

import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class IncidentRepository {

    private static final ObservableList<Incident> incidents =
            FXCollections.observableArrayList();

    static {
        incidents.add(new Incident(
                "INC-001",
                "Hurricane",
                "Miami, Florida",
                "Hurricane Alert with 50+ mph winds",
                "Critical",
                "Active",
                LocalDateTime.now().minusHours(2),
                15
        ));

        incidents.add(new Incident(
                "INC-002",
                "Fire",
                "Downtown Seattle",
                "Building Fire at Main Street Commercial District",
                "High",
                "Active",
                LocalDateTime.now().minusHours(1),
                8
        ));

        incidents.add(new Incident(
                "INC-003",
                "Earthquake",
                "Los Angeles, CA",
                "Earthquake Aftershock magnitude 5.2",
                "High",
                "Active",
                LocalDateTime.now().minusMinutes(30),
                22
        ));

        incidents.add(new Incident(
                "INC-004",
                "Flood",
                "Houston, Texas",
                "Flood Alert due to heavy rainfall",
                "Medium",
                "Active",
                LocalDateTime.now().minusMinutes(45),
                5
        ));

        incidents.add(new Incident(
                "INC-005",
                "Tornado",
                "Oklahoma City",
                "Tornado Warning in effect - evacuate area",
                "Critical",
                "Active",
                LocalDateTime.now().minusMinutes(15),
                30
        ));

        incidents.add(new Incident(
                "INC-006",
                "Tsunami",
                "Pacific Coast",
                "Tsunami Warning - evacuate immediately",
                "Critical",
                "Resolved",
                LocalDateTime.now().minusHours(3),
                12
        ));

        incidents.add(new Incident(
                "INC-007",
                "Landslide",
                "Mountain Region",
                "Landslide Risk - avoid area until cleared",
                "Medium",
                "Resolved",
                LocalDateTime.now().minusHours(4),
                2
        ));

        incidents.add(new Incident(
                "INC-008",
                "Wildfire",
                "California Forests",
                "Wildfire spreading rapidly northward",
                "High",
                "Active",
                LocalDateTime.now().minusMinutes(20),
                0
        ));
    }

    private IncidentRepository() {
        // Prevent creation of repository objects
    }

    public static ObservableList<Incident> getIncidents() {
        return incidents;
    }

    public static void addIncident(Incident incident) {
        incidents.add(incident);
    }

    public static String generateIncidentId() {
        return String.format("INC-%03d", incidents.size() + 1);
    }
}