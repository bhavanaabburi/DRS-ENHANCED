package com.drs.drs.initial.features;

import com.drs.drs.initial.model.Incident;
import com.drs.drs.initial.model.IncidentRepository;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Controller for Priority Analysis screen.
 *
 * Creative Feature #1:
 * Intelligent Priority Analysis.
 *
 * The system calculates a recommended priority based on
 * disaster factors. The coordinator can then confirm the
 * recommendation or select a different priority.
 *
 * @author DRS Development Team
 * @version 3.0
 */
public class PriorityCalculatorController {

    // =========================================================
    // FXML - INCIDENT
    // =========================================================

    @FXML
    private ComboBox<String> incidentComboBox;


    // =========================================================
    // FXML - INPUTS
    // =========================================================

    @FXML
    private ComboBox<String> impactLevelComboBox;

    @FXML
    private Spinner<Integer> peopleAffectedSpinner;

    @FXML
    private ComboBox<String> accessibilityComboBox;

    @FXML
    private Spinner<Integer> minutesSinceReportSpinner;

    @FXML
    private Spinner<Integer> injuredCountSpinner;


    // =========================================================
    // FXML - RESULTS
    // =========================================================

    @FXML
    private Label priorityScoreLabel;

    @FXML
    private Label priorityLevelLabel;

    @FXML
    private Label responseTimeLabel;

    @FXML
    private Label resourceAllocationLabel;

    @FXML
    private TextArea recommendationArea;

    @FXML
    private Label messageLabel;


    // =========================================================
    // FXML - COORDINATOR CONFIRMATION
    // =========================================================

    @FXML
    private ComboBox<String> coordinatorPriorityComboBox;

    @FXML
    private Button confirmPriorityButton;

    @FXML
    private Label confirmationMessageLabel;


    // =========================================================
    // FXML - BUTTONS
    // =========================================================

    @FXML
    private Button calculateButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button backButton;


    // =========================================================
    // CONTROLLER DATA
    // =========================================================

    private Incident selectedIncident;

    private int latestPriorityScore = -1;

    private String latestPriorityLevel;


    // =========================================================
    // INITIALIZATION
    // =========================================================

    @FXML
    public void initialize() {

        System.out.println(
                "[PriorityCalculatorController] Initializing..."
        );

        setupComboBoxes();
        setupSpinners();
        setupIncidentComboBox();
        setupCoordinatorPriorityComboBox();
        setupEventHandlers();

        resetConfirmationSection();

        System.out.println(
                "[PriorityCalculatorController] Initialization complete."
        );
    }


    // =========================================================
    // COMBO BOX SETUP
    // =========================================================

    private void setupComboBoxes() {

        impactLevelComboBox.getItems().setAll(
                "Minor",
                "Moderate",
                "Severe",
                "Critical"
        );

        impactLevelComboBox.setValue("Minor");


        accessibilityComboBox.getItems().setAll(
                "Accessible",
                "Partially Accessible",
                "Not Accessible"
        );

        accessibilityComboBox.setValue("Accessible");
    }


    private void setupCoordinatorPriorityComboBox() {

        coordinatorPriorityComboBox.getItems().setAll(
                "Low",
                "Medium",
                "High",
                "Critical"
        );

        coordinatorPriorityComboBox.setDisable(true);
    }


    // =========================================================
    // SPINNER SETUP
    // =========================================================

    private void setupSpinners() {

        peopleAffectedSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        10000,
                        0,
                        100
                )
        );


        minutesSinceReportSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        1440,
                        0,
                        15
                )
        );


        injuredCountSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        500,
                        0,
                        5
                )
        );
    }


    // =========================================================
    // INCIDENT SETUP
    // =========================================================

    private void setupIncidentComboBox() {

        incidentComboBox.getItems().clear();

        for (Incident incident :
                IncidentRepository.getIncidents()) {

            if (!"Resolved".equalsIgnoreCase(
                    incident.getStatus())
                    &&
                    !"Closed".equalsIgnoreCase(
                            incident.getStatus())) {

                incidentComboBox.getItems().add(
                        incident.getIncidentId()
                );
            }
        }
    }


    // =========================================================
    // EVENT HANDLERS
    // =========================================================

    private void setupEventHandlers() {

        incidentComboBox.setOnAction(
                event -> loadSelectedIncident()
        );


        calculateButton.setOnAction(
                event -> calculatePriority()
        );


        resetButton.setOnAction(
                event -> resetForm()
        );


        backButton.setOnAction(
                event -> openDashboard()
        );


        confirmPriorityButton.setOnAction(
                event -> confirmPriority()
        );
    }


    // =========================================================
    // INCIDENT SELECTION
    // =========================================================

    private void loadSelectedIncident() {

        String incidentId =
                incidentComboBox.getValue();

        if (incidentId == null
                || incidentId.isBlank()) {

            selectedIncident = null;

            return;
        }


        selectedIncident =
                findIncidentById(incidentId);


        if (selectedIncident == null) {

            showError(
                    "Incident could not be found."
            );

            return;
        }


        // Clear old calculation
        clearResults();

        resetConfirmationSection();


        // Load current incident values
        impactLevelComboBox.setValue(
                convertPriorityToImpact(
                        selectedIncident.getPriority()
                )
        );


        setSpinnerValue(
                injuredCountSpinner,
                Math.min(
                        500,
                        Math.max(
                                0,
                                selectedIncident.getInjuredCount()
                        )
                )
        );


        int minutes =
                calculateMinutesSinceReport(
                        selectedIncident.getReportedAt()
                );


        setSpinnerValue(
                minutesSinceReportSpinner,
                minutes
        );


        /*
         * People affected is not currently stored separately
         * inside Incident, so coordinator enters this value.
         */
        setSpinnerValue(
                peopleAffectedSpinner,
                0
        );


        /*
         * Accessibility is also assessment-specific.
         */
        accessibilityComboBox.setValue(
                "Accessible"
        );


        messageLabel.setText(
                "Incident "
                        + selectedIncident.getIncidentId()
                        + " loaded. Current priority: "
                        + selectedIncident.getPriority()
                        + "."
        );

        messageLabel.setStyle(
                "-fx-text-fill: #1F5B99; "
                        + "-fx-font-weight: bold;"
        );


        System.out.println(
                "[INCIDENT SELECTED] "
                        + selectedIncident.getIncidentId()
        );
    }


    private Incident findIncidentById(
            String incidentId) {

        for (Incident incident :
                IncidentRepository.getIncidents()) {

            if (incident
                    .getIncidentId()
                    .equalsIgnoreCase(
                            incidentId
                    )) {

                return incident;
            }
        }

        return null;
    }


    // =========================================================
    // CALCULATE PRIORITY
    // =========================================================

    @FXML
    private void calculatePriority() {

        try {

            if (selectedIncident == null) {

                showError(
                        "Please select an incident first."
                );

                return;
            }


            if ("Resolved".equalsIgnoreCase(
                    selectedIncident.getStatus())
                    ||
                    "Closed".equalsIgnoreCase(
                            selectedIncident.getStatus())) {

                showError(
                        "Priority cannot be calculated for a resolved or closed incident."
                );

                return;
            }


            String impactLevel =
                    impactLevelComboBox.getValue();


            int peopleAffected =
                    peopleAffectedSpinner.getValue();


            String accessibility =
                    accessibilityComboBox.getValue();


            /*
             * Automatically calculate elapsed time using
             * Incident.reportedAt.
             */
            int minutesSinceReport =
                    calculateMinutesSinceReport(
                            selectedIncident.getReportedAt()
                    );


            setSpinnerValue(
                    minutesSinceReportSpinner,
                    minutesSinceReport
            );


            int injuredCount =
                    injuredCountSpinner.getValue();


            if (impactLevel == null
                    || accessibility == null) {

                showError(
                        "Please complete all required fields."
                );

                return;
            }


            if (peopleAffected < 0
                    || injuredCount < 0) {

                showError(
                        "People affected and injured count cannot be negative."
                );

                return;
            }


            // =================================================
            // CALCULATE SCORE
            // =================================================

            int priorityScore =
                    PriorityCalculator
                            .calculatePriorityScore(
                                    impactLevel,
                                    peopleAffected,
                                    accessibility,
                                    minutesSinceReport,
                                    injuredCount
                            );


            String priorityLevel =
                    PriorityCalculator
                            .getPriorityLevel(
                                    priorityScore
                            );


            int responseTime =
                    PriorityCalculator
                            .getRecommendedResponseTime(
                                    priorityScore
                            );


            int resourceAllocation =
                    PriorityCalculator
                            .getRecommendedResourceAllocation(
                                    priorityScore
                            );


            String detailedRecommendation =
                    PriorityCalculator
                            .getDetailedRecommendation(
                                    priorityScore,
                                    priorityLevel
                            );


            // Save latest calculation
            latestPriorityScore =
                    priorityScore;

            latestPriorityLevel =
                    priorityLevel;


            // =================================================
            // DISPLAY RESULTS
            // =================================================

            displayResults(
                    priorityScore,
                    priorityLevel,
                    responseTime,
                    resourceAllocation,
                    detailedRecommendation
            );


            // =================================================
            // ENABLE COORDINATOR CONFIRMATION
            // =================================================

            String recommendedPriority =
                    normalizePriority(
                            priorityLevel
                    );


            coordinatorPriorityComboBox.setDisable(
                    false
            );


            coordinatorPriorityComboBox.setValue(
                    recommendedPriority
            );


            confirmPriorityButton.setDisable(
                    false
            );


            confirmationMessageLabel.setText(
                    "Recommended priority is "
                            + recommendedPriority
                            + ". Confirm it or select a different priority."
            );


            confirmationMessageLabel.setStyle(
                    "-fx-text-fill: #1F5B99; "
                            + "-fx-font-weight: bold;"
            );


            System.out.println(
                    "[PRIORITY CALCULATED] Incident="
                            + selectedIncident.getIncidentId()
                            + ", Score="
                            + priorityScore
                            + ", Recommended="
                            + priorityLevel
            );


        } catch (Exception e) {

            showError(
                    "Error calculating priority: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // COORDINATOR CONFIRMATION
    // =========================================================

    @FXML
    private void confirmPriority() {

        if (selectedIncident == null) {

            showError(
                    "Please select an incident first."
            );

            return;
        }


        if (latestPriorityScore < 0
                || latestPriorityLevel == null) {

            showError(
                    "Please calculate the priority before confirmation."
            );

            return;
        }


        String coordinatorPriority =
                coordinatorPriorityComboBox.getValue();


        if (coordinatorPriority == null) {

            showError(
                    "Please select the coordinator priority."
            );

            return;
        }


        String oldPriority =
                selectedIncident.getPriority();


        /*
         * Incident.setPriority() validates the value.
         * Values must be:
         * Low, Medium, High or Critical.
         */
        selectedIncident.setPriority(
                coordinatorPriority
        );


        String updatedPriority =
                selectedIncident.getPriority();


        // Verify update succeeded
        if (!updatedPriority.equals(
                coordinatorPriority)) {

            showError(
                    "Unable to update incident priority."
            );

            return;
        }


        // =====================================================
        // UPDATE UI
        // =====================================================

        confirmationMessageLabel.setText(
                "Priority confirmed successfully. "
                        + selectedIncident.getIncidentId()
                        + " changed from "
                        + oldPriority
                        + " to "
                        + updatedPriority
                        + "."
        );


        confirmationMessageLabel.setStyle(
                "-fx-text-fill: #28A745; "
                        + "-fx-font-weight: bold;"
        );


        messageLabel.setText(
                "Incident "
                        + selectedIncident.getIncidentId()
                        + " priority updated successfully."
        );


        messageLabel.setStyle(
                "-fx-text-fill: #28A745; "
                        + "-fx-font-weight: bold;"
        );


        /*
         * Update detailed analysis so the screen immediately
         * reflects the coordinator's decision.
         */
        String existingAnalysis =
                recommendationArea.getText();


        recommendationArea.setText(
                existingAnalysis
                        + "\n\n"
                        + "COORDINATOR DECISION\n"
                        + "Recommended Priority: "
                        + normalizePriority(
                                latestPriorityLevel
                        )
                        + "\n"
                        + "Confirmed Priority: "
                        + updatedPriority
        );


        /*
         * Disable confirmation after successful update.
         * User must calculate again before another confirmation.
         */
        confirmPriorityButton.setDisable(
                true
        );


        coordinatorPriorityComboBox.setDisable(
                true
        );


        System.out.println(
                "[PRIORITY CONFIRMED] Incident="
                        + selectedIncident.getIncidentId()
                        + ", Old="
                        + oldPriority
                        + ", Recommended="
                        + latestPriorityLevel
                        + ", Confirmed="
                        + updatedPriority
        );
    }


    // =========================================================
    // DISPLAY RESULTS
    // =========================================================

    private void displayResults(
            int priorityScore,
            String priorityLevel,
            int responseTime,
            int resourceAllocation,
            String detailedRecommendation) {


        priorityScoreLabel.setText(
                priorityScore + " / 100"
        );


        priorityScoreLabel.setStyle(
                getPriorityScoreStyle(
                        priorityScore
                )
        );


        priorityLevelLabel.setText(
                priorityLevel
        );


        priorityLevelLabel.setStyle(
                getPriorityLevelStyle(
                        priorityLevel
                )
        );


        responseTimeLabel.setText(
                "Response Time: "
                        + responseTime
                        + " hour(s)"
        );


        resourceAllocationLabel.setText(
                "Resource Allocation: "
                        + resourceAllocation
                        + "%"
        );


        recommendationArea.setText(
                "Incident ID: "
                        + selectedIncident.getIncidentId()
                        + "\n"
                        + "Disaster Type: "
                        + selectedIncident.getDisasterType()
                        + "\n"
                        + "Location: "
                        + selectedIncident.getLocation()
                        + "\n"
                        + "Current Incident Priority: "
                        + selectedIncident.getPriority()
                        + "\n"
                        + "Recommended Priority: "
                        + priorityLevel
                        + "\n\n"
                        + detailedRecommendation
        );


        recommendationArea.setStyle(
                getRecommendationStyle(
                        priorityLevel
                )
        );


        messageLabel.setText(
                "Priority recommendation calculated for "
                        + selectedIncident.getIncidentId()
                        + "."
        );


        messageLabel.setStyle(
                "-fx-text-fill: #28A745; "
                        + "-fx-font-weight: bold;"
        );
    }


    // =========================================================
    // PRIORITY CONVERSION
    // =========================================================

    /**
     * Convert calculator priority:
     *
     * CRITICAL -> Critical
     * HIGH     -> High
     * MEDIUM   -> Medium
     * LOW      -> Low
     */
    private String normalizePriority(
            String priority) {

        if (priority == null
                || priority.isBlank()) {

            return "Medium";
        }


        switch (priority.toUpperCase()) {

            case "CRITICAL":
                return "Critical";

            case "HIGH":
                return "High";

            case "MEDIUM":
                return "Medium";

            case "LOW":
                return "Low";

            default:
                return "Medium";
        }
    }


    /**
     * Existing incident priority is used as a starting
     * impact level when an incident is selected.
     */
    private String convertPriorityToImpact(
            String priority) {

        if (priority == null) {

            return "Minor";
        }


        switch (priority.toUpperCase()) {

            case "CRITICAL":
                return "Critical";

            case "HIGH":
                return "Severe";

            case "MEDIUM":
                return "Moderate";

            case "LOW":
            default:
                return "Minor";
        }
    }


    // =========================================================
    // TIME
    // =========================================================

    private int calculateMinutesSinceReport(
            LocalDateTime reportedAt) {

        if (reportedAt == null) {

            return 0;
        }


        long minutes =
                Duration.between(
                        reportedAt,
                        LocalDateTime.now()
                ).toMinutes();


        if (minutes < 0) {

            return 0;
        }


        /*
         * Spinner currently supports a maximum of 1440
         * minutes (24 hours).
         */
        if (minutes > 1440) {

            return 1440;
        }


        return (int) minutes;
    }


    // =========================================================
    // SPINNER HELPER
    // =========================================================

    private void setSpinnerValue(
            Spinner<Integer> spinner,
            int value) {

        if (spinner != null
                && spinner.getValueFactory() != null) {

            spinner
                    .getValueFactory()
                    .setValue(value);
        }
    }


    // =========================================================
    // RESET CONFIRMATION
    // =========================================================

    private void resetConfirmationSection() {

        coordinatorPriorityComboBox
                .getSelectionModel()
                .clearSelection();


        coordinatorPriorityComboBox.setDisable(
                true
        );


        confirmPriorityButton.setDisable(
                true
        );


        confirmationMessageLabel.setText(
                "Calculate a priority recommendation before confirmation."
        );


        confirmationMessageLabel.setStyle(
                "-fx-font-size: 12px; "
                        + "-fx-text-fill: #666666;"
        );
    }


    // =========================================================
    // CLEAR RESULTS
    // =========================================================

    private void clearResults() {

        latestPriorityScore = -1;

        latestPriorityLevel = null;


        priorityScoreLabel.setText(
                "--"
        );


        priorityLevelLabel.setText(
                "--"
        );


        responseTimeLabel.setText(
                "--"
        );


        resourceAllocationLabel.setText(
                "--"
        );


        recommendationArea.clear();
    }


    // =========================================================
    // RESET FORM
    // =========================================================

    @FXML
    private void resetForm() {

        selectedIncident = null;

        latestPriorityScore = -1;

        latestPriorityLevel = null;


        incidentComboBox
                .getSelectionModel()
                .clearSelection();


        impactLevelComboBox.setValue(
                "Minor"
        );


        accessibilityComboBox.setValue(
                "Accessible"
        );


        setSpinnerValue(
                peopleAffectedSpinner,
                0
        );


        setSpinnerValue(
                minutesSinceReportSpinner,
                0
        );


        setSpinnerValue(
                injuredCountSpinner,
                0
        );


        clearResults();

        resetConfirmationSection();


        messageLabel.setText(
                "Form reset. Select an incident to begin."
        );


        messageLabel.setStyle(
                "-fx-text-fill: #666666;"
        );
    }


    // =========================================================
    // STYLES
    // =========================================================

    private String getPriorityScoreStyle(
            int score) {

        if (score >= 80) {

            return "-fx-font-size: 28px; "
                    + "-fx-font-weight: bold; "
                    + "-fx-text-fill: #8B0000;";

        } else if (score >= 60) {

            return "-fx-font-size: 28px; "
                    + "-fx-font-weight: bold; "
                    + "-fx-text-fill: #DC3545;";

        } else if (score >= 40) {

            return "-fx-font-size: 28px; "
                    + "-fx-font-weight: bold; "
                    + "-fx-text-fill: #FF9800;";

        } else {

            return "-fx-font-size: 28px; "
                    + "-fx-font-weight: bold; "
                    + "-fx-text-fill: #28A745;";
        }
    }


    private String getPriorityLevelStyle(
            String level) {

        switch (level) {

            case "CRITICAL":

                return "-fx-font-size: 18px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: white; "
                        + "-fx-background-color: #8B0000; "
                        + "-fx-padding: 10;";

            case "HIGH":

                return "-fx-font-size: 18px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: white; "
                        + "-fx-background-color: #DC3545; "
                        + "-fx-padding: 10;";

            case "MEDIUM":

                return "-fx-font-size: 18px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: black; "
                        + "-fx-background-color: #FF9800; "
                        + "-fx-padding: 10;";

            case "LOW":

                return "-fx-font-size: 18px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: white; "
                        + "-fx-background-color: #28A745; "
                        + "-fx-padding: 10;";

            default:

                return "-fx-font-size: 16px;";
        }
    }


    private String getRecommendationStyle(
            String level) {

        switch (level) {

            case "CRITICAL":

                return "-fx-control-inner-background: #FFE6E6; "
                        + "-fx-text-fill: #8B0000; "
                        + "-fx-font-weight: bold;";

            case "HIGH":

                return "-fx-control-inner-background: #FFE0E0; "
                        + "-fx-text-fill: #DC3545;";

            case "MEDIUM":

                return "-fx-control-inner-background: #FFF9E6; "
                        + "-fx-text-fill: #FF9800;";

            case "LOW":

                return "-fx-control-inner-background: #E8F5E9; "
                        + "-fx-text-fill: #28A745;";

            default:

                return "-fx-control-inner-background: #FFFFFF;";
        }
    }


    // =========================================================
    // NAVIGATION
    // =========================================================

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

            showError(
                    "Unable to open Dashboard: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message) {

        messageLabel.setText(
                "ERROR: " + message
        );


        messageLabel.setStyle(
                "-fx-text-fill: #DC3545; "
                        + "-fx-font-weight: bold;"
        );


        System.err.println(
                "[ERROR] " + message
        );
    }
}