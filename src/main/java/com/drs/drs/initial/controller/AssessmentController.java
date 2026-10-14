package com.drs.drs.initial.controller;

import com.drs.drs.initial.model.Assessment;
import com.drs.drs.initial.model.Incident;
import com.drs.drs.initial.model.IncidentRepository;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the Disaster Assessment view.
 *
 * Handles:
 * - Input validation
 * - Incident validation
 * - Assessment calculations
 * - Priority calculation
 * - Resource recommendations
 * - Response-time calculation
 * - Updating the corresponding Incident
 *
 * @author DRS Development Team
 * @version 1.1
 */
public class AssessmentController {

    // ========== INPUT FIELDS ==========

    @FXML
    private TextField incidentIdField;

    @FXML
    private TextField peopleAffectedField;

    @FXML
    private ComboBox<String> impactLevelComboBox;

    @FXML
    private ComboBox<String> accessConditionComboBox;

    @FXML
    private TextField assistanceNeedsField;

    @FXML
    private TextArea assessmentNotesArea;


    // ========== BUTTONS ==========

    @FXML
    private Button assessButton;

    @FXML
    private Button backButton;


    // ========== RESULT FIELDS ==========

    @FXML
    private Label calculatedImpactLevelLabel;

    @FXML
    private Label priorityLabel;

    @FXML
    private Label responseTimeLabel;

    @FXML
    private TextArea requiredResourcesArea;

    @FXML
    private Label messageLabel;

    @FXML
    private TextArea assessmentSummaryArea;


    // ========== CONSTRUCTOR ==========

    public AssessmentController() {
    }


    // ========== INITIALIZE ==========

    @FXML
    private void initialize() {

        impactLevelComboBox.getItems().addAll(
                "Low",
                "Medium",
                "High",
                "Critical"
        );

        accessConditionComboBox.getItems().addAll(
                "Accessible",
                "Partially Accessible",
                "Not Accessible"
        );

        assessButton.setOnAction(
                event -> performAssessment()
        );

        backButton.setOnAction(
                event -> openDashboard()
        );

        clearResults();
    }


    // ========== PERFORM ASSESSMENT ==========

    /**
     * Performs the complete disaster assessment.
     *
     * The method:
     * 1. Validates user input
     * 2. Checks whether the incident exists
     * 3. Creates an Assessment object
     * 4. Performs assessment calculations
     * 5. Updates the incident
     * 6. Displays results
     */
    @FXML
    private void performAssessment() {

        // ---------- Read input ----------

        String incidentId =
                incidentIdField
                        .getText()
                        .trim();

        String peopleText =
                peopleAffectedField
                        .getText()
                        .trim();

        String impactLevel =
                impactLevelComboBox
                        .getValue();

        String accessCondition =
                accessConditionComboBox
                        .getValue();

        String assistanceNeeds =
                assistanceNeedsField
                        .getText()
                        .trim();

        String notes =
                assessmentNotesArea
                        .getText()
                        .trim();


        // ========== REQUIRED FIELD VALIDATION ==========

        if (incidentId.isEmpty()
                || peopleText.isEmpty()
                || impactLevel == null
                || accessCondition == null) {

            showError(
                    "Please complete all required assessment fields."
            );

            clearResults();

            return;
        }


        // ========== PEOPLE AFFECTED VALIDATION ==========

        int peopleAffected;

        try {

            peopleAffected =
                    Integer.parseInt(
                            peopleText
                    );

            if (peopleAffected < 0) {

                showError(
                        "People affected cannot be negative."
                );

                clearResults();

                return;
            }

        } catch (NumberFormatException e) {

            showError(
                    "People affected must be a valid number."
            );

            clearResults();

            return;
        }


        // ========== FIND INCIDENT ==========

        Incident incident =
                findIncidentById(
                        incidentId
                );


        if (incident == null) {

            showError(
                    "Incident "
                    + incidentId
                    + " does not exist."
            );

            clearResults();

            return;
        }


        // ========== CHECK INCIDENT STATUS ==========

        if ("Resolved".equals(
                incident.getStatus())
                || "Closed".equals(
                        incident.getStatus())) {

            showError(
                    "Incident "
                    + incidentId
                    + " cannot be assessed because it is "
                    + incident.getStatus()
                    + "."
            );

            clearResults();

            return;
        }


        // ========== CREATE ASSESSMENT ==========

        Assessment assessment =
                new Assessment(
                        "ASM-"
                        + System.currentTimeMillis(),
                        peopleAffected,
                        impactLevel,
                        accessCondition,
                        assistanceNeeds,
                        notes
                );


        // ========== CALCULATIONS ==========

        String calculatedImpactLevel =
                assessment.calculateImpactLevel();

        String calculatedPriority =
                assessment.calculatePriority();

        String recommendedResources =
                assessment.recommendResources();

        int estimatedResponseTime =
                assessment
                        .calculateEstimatedResponseTime();


        // ========== UPDATE INCIDENT ==========

        String previousPriority =
                incident.getPriority();


        /*
         * Store the calculated priority in the
         * actual incident.
         */
        incident.setPriority(
                calculatedPriority
        );


        /*
         * Store the affected/injured count.
         *
         * For this prototype the assessment's
         * people-affected value is stored in the
         * Incident injuredCount field because the
         * current Incident model does not contain
         * a separate peopleAffected attribute.
         */
        incident.setInjuredCount(
                peopleAffected
        );


        /*
         * Ensure assessed incident remains active.
         */
        if ("Reported".equals(
                incident.getStatus())) {

            incident.activateIncident();
        }


        // ========== DISPLAY RESULTS ==========

        calculatedImpactLevelLabel.setText(
                calculatedImpactLevel
        );

        calculatedImpactLevelLabel.setStyle(
                getImpactLevelStyle(
                        calculatedImpactLevel
                )
        );


        priorityLabel.setText(
                calculatedPriority
        );

        priorityLabel.setStyle(
                getPriorityStyle(
                        calculatedPriority
                )
        );


        responseTimeLabel.setText(
                estimatedResponseTime
                + " hours"
        );

        responseTimeLabel.setStyle(
                "-fx-font-size: 18px; "
                + "-fx-font-weight: bold;"
        );


        requiredResourcesArea.setText(
                formatResources(
                        recommendedResources
                )
        );

        requiredResourcesArea.setStyle(
                "-fx-control-inner-background: #f0f0f0;"
        );


        // ========== SUCCESS MESSAGE ==========

        messageLabel.setText(
                "✅ Assessment completed successfully for incident: "
                + incident.getIncidentId()
                + " | Priority updated from "
                + previousPriority
                + " to "
                + calculatedPriority
        );

        messageLabel.setStyle(
                "-fx-text-fill: green; "
                + "-fx-font-weight: bold;"
        );


        // ========== COMPLETE SUMMARY ==========

        String completeSummary =
                assessment.generateSummary()
                + "\n\n"
                + "===== INCIDENT UPDATE =====\n"
                + "Incident ID: "
                + incident.getIncidentId()
                + "\n"
                + "Disaster Type: "
                + incident.getDisasterType()
                + "\n"
                + "Location: "
                + incident.getLocation()
                + "\n"
                + "Previous Priority: "
                + previousPriority
                + "\n"
                + "Updated Priority: "
                + incident.getPriority()
                + "\n"
                + "Status: "
                + incident.getStatus()
                + "\n"
                + "People Affected: "
                + peopleAffected;


        assessmentSummaryArea.setText(
                completeSummary
        );

        assessmentSummaryArea.setStyle(
                "-fx-control-inner-background: #f9f9f9;"
        );


        // ========== CONSOLE OUTPUT ==========

        System.out.println();
        System.out.println(
                "=".repeat(60)
        );

        System.out.println(
                "ASSESSMENT CALCULATION RESULTS"
        );

        System.out.println(
                "=".repeat(60)
        );

        System.out.println(
                "Incident ID: "
                + incident.getIncidentId()
        );

        System.out.println(
                "Disaster Type: "
                + incident.getDisasterType()
        );

        System.out.println(
                "Location: "
                + incident.getLocation()
        );

        System.out.println(
                "People Affected: "
                + peopleAffected
        );

        System.out.println(
                "Impact Level (User Selected): "
                + impactLevel
        );

        System.out.println(
                "Access Condition: "
                + accessCondition
        );

        System.out.println();

        System.out.println(
                "CALCULATED RESULTS:"
        );

        System.out.println(
                "  Calculated Impact Level: "
                + calculatedImpactLevel
        );

        System.out.println(
                "  Previous Priority: "
                + previousPriority
        );

        System.out.println(
                "  Calculated Priority: "
                + calculatedPriority
        );

        System.out.println(
                "  Recommended Resources: "
                + recommendedResources
        );

        System.out.println(
                "  Estimated Response Time: "
                + estimatedResponseTime
                + " hours"
        );

        System.out.println();

        System.out.println(
                "UPDATED INCIDENT:"
        );

        System.out.println(
                incident
        );

        System.out.println(
                "=".repeat(60)
        );

        System.out.println();
    }


    // ========== FIND INCIDENT ==========

    /**
     * Finds an incident in the shared repository.
     *
     * @param incidentId incident identifier
     * @return matching Incident or null
     */
    private Incident findIncidentById(
            String incidentId) {

        for (Incident incident
                : IncidentRepository
                        .getIncidents()) {

            if (incident
                    .getIncidentId()
                    .equalsIgnoreCase(
                            incidentId)) {

                return incident;
            }
        }

        return null;
    }


    // ========== CLEAR RESULTS ==========

    private void clearResults() {

        calculatedImpactLevelLabel.setText(
                "Not assessed"
        );

        priorityLabel.setText(
                "Not assessed"
        );

        responseTimeLabel.setText(
                "-- hours"
        );

        requiredResourcesArea.clear();

        assessmentSummaryArea.clear();
    }


    // ========== RESOURCE FORMATTING ==========

    /**
     * Formats comma-separated resources
     * into a bullet list.
     */
    private String formatResources(
            String resources) {

        if (resources == null
                || resources.isEmpty()) {

            return "No resources recommended";
        }


        String[] resourceArray =
                resources.split(",\\s*");


        StringBuilder formatted =
                new StringBuilder();


        for (int i = 0;
                i < resourceArray.length;
                i++) {

            formatted
                    .append("• ")
                    .append(
                            resourceArray[i]
                                    .trim()
                    );


            if (i
                    < resourceArray.length - 1) {

                formatted.append("\n");
            }
        }


        return formatted.toString();
    }


    // ========== IMPACT STYLE ==========

    private String getImpactLevelStyle(
            String impactLevel) {

        switch (impactLevel) {

            case "Critical":

                return "-fx-font-size: 18px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: darkred;";


            case "High":

                return "-fx-font-size: 18px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: red;";


            case "Medium":

                return "-fx-font-size: 18px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: darkorange;";


            case "Low":

            default:

                return "-fx-font-size: 18px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: green;";
        }
    }


    // ========== PRIORITY STYLE ==========

    private String getPriorityStyle(
            String priority) {

        switch (priority) {

            case "Critical":

                return "-fx-font-size: 20px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: darkred;";


            case "High":

                return "-fx-font-size: 20px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: red;";


            case "Medium":

                return "-fx-font-size: 20px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: darkorange;";


            case "Low":

            default:

                return "-fx-font-size: 20px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-text-fill: green;";
        }
    }


    // ========== ERROR MESSAGE ==========

    private void showError(
            String message) {

        messageLabel.setText(
                "❌ ERROR: "
                + message
        );

        messageLabel.setStyle(
                "-fx-text-fill: red; "
                + "-fx-font-weight: bold;"
        );
    }


    // ========== NAVIGATION ==========

    @FXML
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

            e.printStackTrace();

            showError(
                    "Unable to open Dashboard."
            );
        }
    }
}