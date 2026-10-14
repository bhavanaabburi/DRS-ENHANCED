package com.drs.drs.initial.controller;

import com.drs.drs.initial.model.Incident;
import com.drs.drs.initial.model.IncidentRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for Incidents screen.
 * Displays and manages active incidents in the Disaster Response System.
 *
 * @author DRS Development Team
 * @version 1.0
 */
public class IncidentsController {

    // ========== FXML Components ==========

    @FXML
    private VBox incidentsList;

    @FXML
    private Label messageLabel;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<String> filterComboBox;


    // ========== Data ==========

    private ObservableList<Incident> incidents;


    // ========== Initialize ==========

    /**
     * Initialize the controller when FXML is loaded.
     */
    @FXML
    public void initialize() {

        System.out.println(
                "[IncidentsController] Initializing..."
        );

        setupFilterComboBox();

        initializeIncidents();

        displayIncidents();

        setupEventHandlers();

        System.out.println(
                "[IncidentsController] Initialization complete"
        );
    }


    // ========== Incident Data ==========

    /**
     * Loads incidents from the shared IncidentRepository.
     *
     * This allows incidents created from the Report Disaster
     * screen to also appear in the View Incidents screen.
     */
    private void initializeIncidents() {

        incidents = IncidentRepository.getIncidents();

        System.out.println(
                "[IncidentsController] Loaded "
                + incidents.size()
                + " incidents from repository"
        );
    }


    // ========== Filter Setup ==========

    /**
     * Setup filter ComboBox.
     */
    private void setupFilterComboBox() {

        filterComboBox.setItems(
                FXCollections.observableArrayList(
                        "All Incidents",
                        "Active",
                        "Critical",
                        "High",
                        "Medium",
                        "Low"
                )
        );

        filterComboBox.setValue("All Incidents");

        filterComboBox.setOnAction(
                event -> filterIncidents()
        );
    }


    // ========== Event Handlers ==========

    /**
     * Setup button event handlers.
     */
    private void setupEventHandlers() {

        backButton.setOnAction(
                event -> goBackToDashboard()
        );
    }


    // ========== Display Incidents ==========

    /**
     * Display all incidents as cards.
     */
    private void displayIncidents() {

        incidentsList.getChildren().clear();

        if (incidents == null || incidents.isEmpty()) {

            Label noDataLabel =
                    new Label("No incidents found");

            noDataLabel.setStyle(
                    "-fx-font-size: 14px; "
                    + "-fx-text-fill: #999999;"
            );

            incidentsList
                    .getChildren()
                    .add(noDataLabel);

            messageLabel.setText(
                    "Active Incidents: 0"
            );

            return;
        }


        for (Incident incident : incidents) {

            incidentsList
                    .getChildren()
                    .add(
                            createIncidentCard(incident)
                    );
        }


        messageLabel.setText(
                "Active Incidents: "
                + incidents.size()
        );

        messageLabel.setStyle(
                "-fx-text-fill: #FF6B6B; "
                + "-fx-font-weight: bold;"
        );
    }


    // ========== Incident Card ==========

    /**
     * Creates an incident card UI component.
     *
     * @param incident Incident to display
     * @return VBox containing incident details
     */
    private VBox createIncidentCard(
            Incident incident) {

        VBox card = new VBox(10);

        card.setStyle(
                "-fx-border-color: "
                + getPriorityBorderColor(
                        incident.getPriority())
                + "; "
                + "-fx-border-width: 2; "
                + "-fx-border-radius: 5; "
                + "-fx-padding: 15; "
                + "-fx-background-color: "
                + getPriorityBackgroundColor(
                        incident.getPriority())
                + "; "
                + "-fx-effect: dropshadow("
                + "gaussian, rgba(0,0,0,0.1), "
                + "5, 0, 0, 2);"
        );


        // Incident title
        Label titleLabel =
                new Label(
                        incident.getDisasterType()
                        + " - "
                        + incident.getDescription()
                );

        titleLabel.setStyle(
                "-fx-font-size: 14px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: #1F5B99;"
        );


        // Incident ID and type
        Label detailsLabel =
                new Label(
                        "ID: "
                        + incident.getIncidentId()
                        + " | Type: "
                        + incident.getDisasterType()
                );

        detailsLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #666666;"
        );


        // Location
        Label locationLabel =
                new Label(
                        "Location: "
                        + incident.getLocation()
                );

        locationLabel.setStyle(
                "-fx-font-size: 12px; "
                + "-fx-text-fill: #555555;"
        );


        // Priority and status
        Label priorityLabel =
                new Label(
                        "Priority: "
                        + incident.getPriority()
                        + " | Status: "
                        + incident.getStatus()
                );

        priorityLabel.setStyle(
                "-fx-font-size: 12px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: "
                + getPriorityTextColor(
                        incident.getPriority())
                + ";"
        );


        // Injured count
        Label injuredLabel =
                new Label(
                        "Injured: "
                        + incident.getInjuredCount()
                );

        injuredLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #DC3545;"
        );


        // Time since incident
        long minutesElapsed =
                incident.getMinutesElapsed();

        String timeText;

        if (minutesElapsed < 60) {

            timeText =
                    minutesElapsed
                    + " minutes ago";

        } else if (minutesElapsed < 1440) {

            timeText =
                    (minutesElapsed / 60)
                    + " hours ago";

        } else {

            timeText =
                    (minutesElapsed / 1440)
                    + " days ago";
        }


        Label timeLabel =
                new Label(
                        "Reported: "
                        + timeText
                );

        timeLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #999999;"
        );


        // Incident summary
        Label summaryLabel =
                new Label(
                        incident.getSummary()
                );

        summaryLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #444444;"
        );

        summaryLabel.setWrapText(true);


        // Add components to card
        card.getChildren().addAll(
                titleLabel,
                detailsLabel,
                locationLabel,
                priorityLabel,
                injuredLabel,
                timeLabel,
                new Separator(),
                summaryLabel
        );


        return card;
    }


    // ========== Priority Colours ==========

    /**
     * Gets border colour based on priority.
     */
    private String getPriorityBorderColor(
            String priority) {

        switch (priority) {

            case "Critical":
                return "#8B0000";

            case "High":
                return "#FF6B6B";

            case "Medium":
                return "#FFC107";

            case "Low":
                return "#4CAF50";

            default:
                return "#CCCCCC";
        }
    }


    /**
     * Gets background colour based on priority.
     */
    private String getPriorityBackgroundColor(
            String priority) {

        switch (priority) {

            case "Critical":
                return "#FFE6E6";

            case "High":
                return "#FFE0E0";

            case "Medium":
                return "#FFF9E6";

            case "Low":
                return "#E8F5E9";

            default:
                return "#FFFFFF";
        }
    }


    /**
     * Gets text colour based on priority.
     */
    private String getPriorityTextColor(
            String priority) {

        switch (priority) {

            case "Critical":
                return "#8B0000";

            case "High":
                return "#DC3545";

            case "Medium":
                return "#FF9800";

            case "Low":
                return "#28A745";

            default:
                return "#333333";
        }
    }


    // ========== Filtering ==========

    /**
     * Filter incidents by status or priority.
     */
    @FXML
    private void filterIncidents() {

        String selectedFilter =
                filterComboBox.getValue();


        if ("All Incidents".equals(
                selectedFilter)) {

            displayIncidents();

        } else if ("Active".equals(
                selectedFilter)) {

            ObservableList<Incident> filtered =
                    FXCollections.observableArrayList();


            for (Incident incident : incidents) {

                if ("Active".equals(
                        incident.getStatus())) {

                    filtered.add(incident);
                }
            }


            displayFilteredIncidents(
                    filtered,
                    selectedFilter
            );

        } else {

            ObservableList<Incident> filtered =
                    FXCollections.observableArrayList();


            for (Incident incident : incidents) {

                if (incident
                        .getPriority()
                        .equals(selectedFilter)) {

                    filtered.add(incident);
                }
            }


            displayFilteredIncidents(
                    filtered,
                    selectedFilter
            );
        }
    }


    /**
     * Display filtered incidents.
     */
    private void displayFilteredIncidents(
            ObservableList<Incident> filtered,
            String filter) {

        incidentsList
                .getChildren()
                .clear();


        if (filtered.isEmpty()) {

            Label noResultsLabel =
                    new Label(
                            "No incidents found for: "
                            + filter
                    );

            noResultsLabel.setStyle(
                    "-fx-font-size: 14px; "
                    + "-fx-text-fill: #999999;"
            );

            incidentsList
                    .getChildren()
                    .add(noResultsLabel);


            messageLabel.setText(
                    "No incidents matching: "
                    + filter
            );

            messageLabel.setStyle(
                    "-fx-text-fill: #999999;"
            );

            return;
        }


        for (Incident incident : filtered) {

            incidentsList
                    .getChildren()
                    .add(
                            createIncidentCard(incident)
                    );
        }


        messageLabel.setText(
                "Found "
                + filtered.size()
                + " incident(s)"
        );

        messageLabel.setStyle(
                "-fx-text-fill: #FF6B6B; "
                + "-fx-font-weight: bold;"
        );
    }


    // ========== Navigation ==========

    /**
     * Navigate back to Dashboard.
     */
    @FXML
    private void goBackToDashboard() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/view/Dashboard.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage) backButton
                            .getScene()
                            .getWindow();

            stage.setScene(
                    new Scene(root)
            );

            stage.setTitle(
                    "DRS - Dashboard"
            );

            System.out.println(
                    "[IncidentsController] "
                    + "Navigated back to Dashboard"
            );

        } catch (IOException e) {

            System.err.println(
                    "[ERROR] Failed to load "
                    + "Dashboard: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}