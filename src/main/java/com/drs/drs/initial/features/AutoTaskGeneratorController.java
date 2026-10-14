package com.drs.drs.initial.features;

import com.drs.drs.initial.model.Incident;
import com.drs.drs.initial.model.IncidentRepository;
import com.drs.drs.initial.model.ResponseTask;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Auto Task Generator screen.
 *
 * Creative Feature #2:
 * Intelligent Response Task Generation.
 *
 * Uses real incidents from IncidentRepository and
 * stores generated tasks in ResponseTaskRepository
 * through AutoTaskGenerator.
 *
 * @author DRS Development Team
 * @version 3.0
 */
public class AutoTaskGeneratorController {

    // ========== FXML INPUTS ==========

    @FXML
    private ComboBox<String> incidentComboBox;

    @FXML
    private ComboBox<String> disasterTypeComboBox;

    @FXML
    private ComboBox<String> priorityComboBox;


    // ========== FXML RESULTS ==========

    @FXML
    private Label taskCountLabel;

    @FXML
    private TextArea summaryArea;

    @FXML
    private VBox tasksListContainer;

    @FXML
    private Label messageLabel;


    // ========== BUTTONS ==========

    @FXML
    private Button generateButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button backButton;


    // ========== DATA ==========

    private List<ResponseTask> generatedTasks;

    private Incident selectedIncident;


    // ========== INITIALIZE ==========

    @FXML
    public void initialize() {

        System.out.println(
                "[AutoTaskGeneratorController] Initializing..."
        );

        setupIncidentComboBox();

        setupEventHandlers();

        clearIncidentDetails();

        System.out.println(
                "[AutoTaskGeneratorController] Initialization complete"
        );
    }


    // ========== INCIDENT SETUP ==========

    /**
     * Loads active incidents from the shared
     * IncidentRepository.
     */
    private void setupIncidentComboBox() {

        incidentComboBox.getItems().clear();

        for (Incident incident :
                IncidentRepository.getIncidents()) {

            if (!isClosedIncident(incident)) {

                incidentComboBox.getItems().add(
                        incident.getIncidentId()
                );
            }
        }

        incidentComboBox.setPromptText(
                "Select an active incident"
        );

        incidentComboBox.setOnAction(
                event -> loadSelectedIncident()
        );
    }


    // ========== EVENT HANDLERS ==========

    private void setupEventHandlers() {

        if (generateButton != null) {

            generateButton.setOnAction(
                    event -> generateTasks()
            );
        }

        if (resetButton != null) {

            resetButton.setOnAction(
                    event -> resetForm()
            );
        }

        if (backButton != null) {

            backButton.setOnAction(
                    event -> openDashboard()
            );
        }
    }


    // ========== LOAD INCIDENT ==========

    /**
     * Finds the selected incident in IncidentRepository
     * and automatically loads its disaster type
     * and priority.
     */
    private void loadSelectedIncident() {

        String incidentId =
                incidentComboBox.getValue();

        if (incidentId == null
                || incidentId.isBlank()) {

            selectedIncident = null;

            clearIncidentDetails();

            return;
        }


        selectedIncident =
                findIncidentById(
                        incidentId
                );


        if (selectedIncident == null) {

            clearIncidentDetails();

            showError(
                    "Incident could not be found."
            );

            return;
        }


        String disasterType =
                selectedIncident.getDisasterType();

        String priority =
                selectedIncident.getPriority();


        // Load the actual values into the disabled
        // ComboBoxes for display.

        disasterTypeComboBox.getItems().clear();

        disasterTypeComboBox.getItems().add(
                disasterType
        );

        disasterTypeComboBox.setValue(
                disasterType
        );


        priorityComboBox.getItems().clear();

        priorityComboBox.getItems().add(
                priority
        );

        priorityComboBox.setValue(
                priority
        );


        updateTaskCount();


        messageLabel.setText(
                "Incident "
                + selectedIncident.getIncidentId()
                + " loaded: "
                + disasterType
                + " | Priority: "
                + priority
        );


        messageLabel.setStyle(
                "-fx-text-fill: #1F5B99; "
                + "-fx-font-weight: bold;"
        );


        tasksListContainer
                .getChildren()
                .clear();


        summaryArea.clear();
    }


    // ========== FIND INCIDENT ==========

    private Incident findIncidentById(
            String incidentId) {

        if (incidentId == null) {

            return null;
        }


        for (Incident incident :
                IncidentRepository.getIncidents()) {

            if (incident.getIncidentId()
                    .equalsIgnoreCase(
                            incidentId.trim()
                    )) {

                return incident;
            }
        }


        return null;
    }


    // ========== CLOSED CHECK ==========

    private boolean isClosedIncident(
            Incident incident) {

        if (incident == null
                || incident.getStatus() == null) {

            return false;
        }


        String status =
                incident.getStatus()
                        .trim();


        return status.equalsIgnoreCase(
                    "Resolved"
                )
                || status.equalsIgnoreCase(
                    "Closed"
                );
    }


    // ========== TASK COUNT ==========

    private void updateTaskCount() {

        if (selectedIncident == null) {

            taskCountLabel.setText(
                    "Expected Tasks: --"
            );

            return;
        }


        int count =
                AutoTaskGenerator.getTaskCount(
                        selectedIncident
                                .getDisasterType()
                );


        taskCountLabel.setText(
                "Expected Tasks: "
                + count
        );


        taskCountLabel.setStyle(
                "-fx-font-size: 13px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: #2196F3;"
        );
    }


    // ========== GENERATE REAL TASKS ==========

    /**
     * Generates real ResponseTask objects for
     * the selected incident.
     *
     * AutoTaskGenerator adds these tasks to the
     * shared ResponseTaskRepository.
     */
    @FXML
    private void generateTasks() {

        try {

            if (selectedIncident == null) {

                showError(
                        "Please select an incident first."
                );

                return;
            }


            if (isClosedIncident(
                    selectedIncident)) {

                showError(
                        "Tasks cannot be generated "
                        + "for a resolved or closed incident."
                );

                return;
            }


            String incidentId =
                    selectedIncident
                            .getIncidentId();


            String disasterType =
                    selectedIncident
                            .getDisasterType();


            String priority =
                    selectedIncident
                            .getPriority();


            generatedTasks =
                    AutoTaskGenerator.generateTasks(
                            incidentId,
                            disasterType,
                            priority
                    );


            if (generatedTasks == null
                    || generatedTasks.isEmpty()) {

                showError(
                        "No tasks were generated."
                );

                return;
            }


            displayGeneratedTasks();


            displaySummary(
                    incidentId,
                    disasterType,
                    priority
            );


            messageLabel.setText(
                    "Successfully generated and saved "
                    + generatedTasks.size()
                    + " response tasks for "
                    + incidentId
                    + "."
            );


            messageLabel.setStyle(
                    "-fx-text-fill: #28A745; "
                    + "-fx-font-weight: bold;"
            );


            System.out.println(
                    "[AUTO TASK GENERATOR] Generated "
                    + generatedTasks.size()
                    + " tasks for "
                    + incidentId
            );


        } catch (Exception e) {

            showError(
                    "Error generating tasks: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // ========== DISPLAY TASKS ==========

    private void displayGeneratedTasks() {

        tasksListContainer
                .getChildren()
                .clear();


        for (int i = 0;
                i < generatedTasks.size();
                i++) {

            ResponseTask task =
                    generatedTasks.get(i);


            VBox card =
                    createTaskCard(
                            task,
                            i + 1
                    );


            tasksListContainer
                    .getChildren()
                    .add(
                            card
                    );
        }
    }


    // ========== TASK CARD ==========

    private VBox createTaskCard(
            ResponseTask task,
            int index) {

        VBox card =
                new VBox(8);


        card.setStyle(
                "-fx-border-color: #2196F3; "
                + "-fx-border-width: 2; "
                + "-fx-border-radius: 5; "
                + "-fx-padding: 12; "
                + "-fx-background-color: #E3F2FD;"
        );


        Label numberLabel =
                new Label(
                        "Task "
                        + index
                        + " | ID: "
                        + task.getTaskId()
                );


        numberLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #0D47A1; "
                + "-fx-font-weight: bold;"
        );


        Label incidentLabel =
                new Label(
                        "Incident: "
                        + task.getIncidentId()
                );


        incidentLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: #9C27B0;"
        );


        Label descriptionLabel =
                new Label(
                        task.getDescription()
                );


        descriptionLabel.setStyle(
                "-fx-font-size: 12px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: #1F5B99;"
        );


        descriptionLabel.setWrapText(
                true
        );


        Label departmentLabel =
                new Label(
                        "Assigned to: "
                        + task.getAssignedDepartment()
                );


        departmentLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #555555;"
        );


        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM yyyy HH:mm"
                );


        String targetText =
                task.getTargetTime() == null
                        ? "Not specified"
                        : task.getTargetTime()
                                .format(
                                        formatter
                                );


        Label timeLabel =
                new Label(
                        "Target Completion: "
                        + targetText
                );


        timeLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #666666;"
        );


        Label statusLabel =
                new Label(
                        "Status: "
                        + task.getStatus()
                );


        statusLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #FF9800; "
                + "-fx-font-weight: bold;"
        );


        card.getChildren().addAll(
                numberLabel,
                incidentLabel,
                descriptionLabel,
                departmentLabel,
                timeLabel,
                statusLabel
        );


        return card;
    }


    // ========== SUMMARY ==========

    private void displaySummary(
            String incidentId,
            String disasterType,
            String priority) {

        String summary =
                AutoTaskGenerator.getSummary(
                        incidentId,
                        disasterType,
                        priority,
                        generatedTasks
                );


        summaryArea.setText(
                summary
        );


        summaryArea.setStyle(
                "-fx-control-inner-background: #F5F5F5; "
                + "-fx-text-fill: #333333;"
        );
    }


    // ========== CLEAR INCIDENT DETAILS ==========

    private void clearIncidentDetails() {

        selectedIncident = null;

        disasterTypeComboBox
                .getItems()
                .clear();

        disasterTypeComboBox
                .setValue(null);


        priorityComboBox
                .getItems()
                .clear();

        priorityComboBox
                .setValue(null);


        taskCountLabel.setText(
                "Expected Tasks: --"
        );
    }


    // ========== RESET ==========

    @FXML
    private void resetForm() {

        incidentComboBox
                .getSelectionModel()
                .clearSelection();


        clearIncidentDetails();


        generatedTasks = null;


        tasksListContainer
                .getChildren()
                .clear();


        summaryArea.clear();


        messageLabel.setText(
                "Select an active incident, "
                + "then click Generate Tasks."
        );


        messageLabel.setStyle(
                "-fx-text-fill: #666666;"
        );
    }


    // ========== DASHBOARD ==========

    private void openDashboard() {

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


        } catch (IOException e) {

            System.err.println(
                    "[ERROR] Failed to load Dashboard: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // ========== ERROR ==========

    private void showError(
            String message) {

        messageLabel.setText(
                "ERROR: "
                + message
        );


        messageLabel.setStyle(
                "-fx-text-fill: #DC3545; "
                + "-fx-font-weight: bold;"
        );


        System.err.println(
                "[ERROR] "
                + message
        );
    }
}