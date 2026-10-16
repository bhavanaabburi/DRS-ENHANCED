package com.drs.drs.initial.controller;

import com.drs.drs.initial.model.DisasterReport;
import com.drs.drs.initial.model.Incident;
import com.drs.drs.initial.model.IncidentRepository;
import com.drs.drs.initial.server.DisasterReportClient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Controller for Report Disaster screen.
 * Handles disaster reporting, validation,
 * report assessment and incident creation.
 *
 * @author DRS Development Team
 * @version 1.1
 */
public class ReportDisasterController {

    // ========== FXML Components ==========

    @FXML
    private TextField reportedByField;

    @FXML
    private ComboBox<String> disasterTypeComboBox;

    @FXML
    private TextField locationField;

    @FXML
    private ComboBox<String> impactLevelComboBox;

    @FXML
    private TextArea assistanceNeedsArea;


    // Results display

    @FXML
    private Label requiresAttentionLabel;

    @FXML
    private Label submittedTimeLabel;

    @FXML
    private TextArea assistanceNeedsDisplay;

    @FXML
    private TextArea reportSummaryArea;

    @FXML
    private Label messageLabel;


    // Buttons

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    @FXML
    private Button clearButton;


    // ========== Constants ==========

    private static final String[] DISASTER_TYPES = {
        "Hurricane",
        "Fire",
        "Earthquake",
        "Flood",
        "Tornado",
        "Tsunami",
        "Landslide",
        "Wildfire",
        "Winter Storm",
        "Other"
    };


    private static final String[] IMPACT_LEVELS = {
        "Minor",
        "Moderate",
        "Severe",
        "Critical"
    };


    // ========== Initialize ==========

    @FXML
    public void initialize() {

        setupDisasterTypeComboBox();
        setupImpactLevelComboBox();
        setupEventHandlers();

        clearForm();
    }


    private void setupDisasterTypeComboBox() {

        disasterTypeComboBox
                .getItems()
                .addAll(DISASTER_TYPES);

        disasterTypeComboBox.setStyle(
                "-fx-font-size: 12px;"
        );
    }


    private void setupImpactLevelComboBox() {

        impactLevelComboBox
                .getItems()
                .addAll(IMPACT_LEVELS);

        impactLevelComboBox.setStyle(
                "-fx-font-size: 12px;"
        );
    }


    private void setupEventHandlers() {

        submitButton.setOnAction(
                event -> submitReport()
        );

        backButton.setOnAction(
                event -> goBackToDashboard()
        );

        clearButton.setOnAction(
                event -> clearForm()
        );
    }


    // ========== Submit Report ==========

    /**
     * Validate the form, create a DisasterReport,
     * and create a corresponding Incident.
     */
    @FXML
    private void submitReport() {

        if (!validateInputs()) {
            return;
        }


        // Collect form data

        String reportedBy =
                reportedByField
                        .getText()
                        .trim();

        String disasterType =
                disasterTypeComboBox
                        .getValue();

        String location =
                locationField
                        .getText()
                        .trim();

        String impactLevel =
                impactLevelComboBox
                        .getValue();

        String assistanceNeeds =
                assistanceNeedsArea
                        .getText()
                        .trim();


        // Save both records atomically through the multithreaded server.
        // The server generates both IDs and returns them only after commit.
        final String[] saved;
        try {
            saved = DisasterReportClient.submit(
                    reportedBy, disasterType, location, impactLevel, assistanceNeeds);
        } catch (Exception e) {
            showError("Report not saved: " + e.getMessage());
            return;
        }

        LocalDateTime submittedAt = LocalDateTime.parse(saved[3]);
        DisasterReport report = new DisasterReport(
                saved[1], reportedBy, disasterType, location,
                impactLevel, assistanceNeeds, submittedAt);
        Incident incident = new Incident(
                saved[2], disasterType, location,
                createIncidentDescription(disasterType, assistanceNeeds),
                convertImpactToPriority(impactLevel), "Active", submittedAt, 0);

        // Refresh the shared ObservableList through the server.
        // A refresh failure must not falsely report a failed submission.
        try {
            IncidentRepository.loadIncidents();
        } catch (Exception refreshError) {
            System.err.println("Report saved, but incident refresh failed: "
                    + refreshError.getMessage());
        }

        // ========== Display Result ==========

        displayReportResults(
                report,
                incident
        );


        // ========== Console Logging ==========

        System.out.println(
                "[REPORT SUBMITTED]"
        );

        System.out.println(
                "  Report ID: "
                + report.getDisasterReportId()
        );

        System.out.println(
                "  Incident ID: "
                + incident.getIncidentId()
        );

        System.out.println(
                "  Type: "
                + report.getDisasterType()
        );

        System.out.println(
                "  Location: "
                + report.getLocation()
        );

        System.out.println(
                "  Impact: "
                + report.getImpactLevel()
        );

        System.out.println(
                "  Priority: "
                + incident.getPriority()
        );

        System.out.println(
                "  Requires Attention: "
                + report
                        .requiresImmediateAttention()
        );

        System.out.println(
                "  Submitted: "
                + report
                        .getFormattedSubmittedAt()
        );
    }


    // ========== Validation ==========

    private boolean validateInputs() {

        if (reportedByField
                .getText()
                .trim()
                .isEmpty()) {

            showError(
                    "Please enter who is reporting the disaster"
            );

            return false;
        }


        if (disasterTypeComboBox
                .getValue() == null) {

            showError(
                    "Please select a disaster type"
            );

            return false;
        }


        if (locationField
                .getText()
                .trim()
                .isEmpty()) {

            showError(
                    "Please enter the location of the disaster"
            );

            return false;
        }


        if (impactLevelComboBox
                .getValue() == null) {

            showError(
                    "Please select an impact level"
            );

            return false;
        }


        if (assistanceNeedsArea
                .getText()
                .trim()
                .isEmpty()) {

            showError(
                    "Please describe assistance needs"
            );

            return false;
        }


        return true;
    }


    // ========== Priority Mapping ==========

    /**
     * Convert the report impact level into
     * an initial incident priority.
     */
    private String convertImpactToPriority(
            String impactLevel) {

        switch (impactLevel) {

            case "Critical":
                return "Critical";

            case "Severe":
                return "High";

            case "Moderate":
                return "Medium";

            case "Minor":
                return "Low";

            default:
                return "Low";
        }
    }


    // ========== Incident Description ==========

    /**
     * Creates a short description for the
     * incident list.
     */
    private String createIncidentDescription(
            String disasterType,
            String assistanceNeeds) {

        String cleanedNeeds =
                assistanceNeeds
                        .replace("\n", ", ")
                        .replace(";", ", ");

        return disasterType
                + " reported - Assistance required: "
                + cleanedNeeds;
    }


    // ========== Results Display ==========

    private void displayReportResults(
            DisasterReport report,
            Incident incident) {

        boolean needsAttention =
                report.requiresImmediateAttention();


        String attentionText =
                needsAttention
                ? "🚨 IMMEDIATE ATTENTION REQUIRED"
                : "✓ Standard Response";


        String attentionStyle =
                needsAttention
                ? "-fx-text-fill: #FF0000; "
                  + "-fx-font-weight: bold; "
                  + "-fx-font-size: 14px;"
                : "-fx-text-fill: #28A745; "
                  + "-fx-font-size: 12px;";


        requiresAttentionLabel.setText(
                attentionText
        );

        requiresAttentionLabel.setStyle(
                attentionStyle
        );


        submittedTimeLabel.setText(
                "Submitted: "
                + report.getFormattedSubmittedAt()
                + " | Incident ID: "
                + incident.getIncidentId()
        );


        String[] assistanceArray =
                report.getAssistanceNeedsArray();

        String formattedNeeds =
                formatAssistanceNeeds(
                        assistanceArray
                );


        assistanceNeedsDisplay.setText(
                formattedNeeds
        );

        assistanceNeedsDisplay.setStyle(
                "-fx-control-inner-background: #F0F0F0;"
        );


        String summary =
                generateReportSummary(
                        report,
                        incident
                );


        reportSummaryArea.setText(
                summary
        );

        reportSummaryArea.setStyle(
                getReportStyle(
                        report.getImpactLevel()
                )
        );


        messageLabel.setText(
                "✅ Disaster report submitted successfully! "
                + "Incident "
                + incident.getIncidentId()
                + " created."
        );

        messageLabel.setStyle(
                "-fx-text-fill: #28A745; "
                + "-fx-font-weight: bold;"
        );
    }


    // ========== Assistance Needs Formatting ==========

    private String formatAssistanceNeeds(
            String[] needs) {

        if (needs == null
                || needs.length == 0) {

            return "No specific needs identified";
        }


        StringBuilder formatted =
                new StringBuilder();


        for (String need : needs) {

            if (need != null
                    && !need.trim().isEmpty()) {

                formatted
                        .append("• ")
                        .append(need.trim())
                        .append("\n");
            }
        }


        return formatted.toString();
    }


    // ========== Report Summary ==========

    private String generateReportSummary(
            DisasterReport report,
            Incident incident) {

        return "=== DISASTER REPORT SUMMARY ===\n\n"
                + "Report ID: "
                + report.getDisasterReportId()
                + "\n"

                + "Incident ID: "
                + incident.getIncidentId()
                + "\n"

                + "Reported By: "
                + report.getReportedBy()
                + "\n"

                + "Disaster Type: "
                + report.getDisasterType()
                + "\n"

                + "Location: "
                + report.getLocation()
                + "\n"

                + "Impact Level: "
                + report.getImpactLevel()
                + "\n"

                + "Initial Priority: "
                + incident.getPriority()
                + "\n"

                + "Incident Status: "
                + incident.getStatus()
                + "\n"

                + "Requires Immediate Attention: "
                + (report.requiresImmediateAttention()
                        ? "YES"
                        : "NO")
                + "\n"

                + "Submitted: "
                + report.getFormattedSubmittedAt()
                + "\n"

                + "Minutes Since Submission: "
                + report.getMinutesSinceSubmission()
                + "\n\n"

                + "ASSISTANCE NEEDS:\n"

                + formatAssistanceNeeds(
                        report.getAssistanceNeedsArray()
                )

                + "\n=================================";
    }


    // ========== Report Styling ==========

    private String getReportStyle(
            String impactLevel) {

        switch (impactLevel) {

            case "Critical":

                return "-fx-control-inner-background: #FFE6E6; "
                        + "-fx-text-fill: #8B0000;";


            case "Severe":

                return "-fx-control-inner-background: #FFE4B5; "
                        + "-fx-text-fill: #B22222;";


            case "Moderate":

                return "-fx-control-inner-background: #FFF8DC; "
                        + "-fx-text-fill: #CC6600;";


            case "Minor":

                return "-fx-control-inner-background: #E6F3FF; "
                        + "-fx-text-fill: #0066CC;";


            default:

                return "-fx-control-inner-background: #F5F5F5;";
        }
    }


    // ========== Clear Form ==========

    @FXML
    private void clearForm() {

        reportedByField.clear();

        disasterTypeComboBox.setValue(
                null
        );

        locationField.clear();

        impactLevelComboBox.setValue(
                null
        );

        assistanceNeedsArea.clear();

        requiresAttentionLabel.setText(
                ""
        );

        submittedTimeLabel.setText(
                ""
        );

        assistanceNeedsDisplay.clear();

        reportSummaryArea.clear();

        messageLabel.setText(
                ""
        );
    }


    // ========== Navigation ==========

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


        } catch (IOException e) {

            showError(
                    "Failed to load Dashboard: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    // ========== Utility Methods ==========

    private String generateReportId() {

        return "RPT-"
                + System.currentTimeMillis();
    }


    private void showError(
            String message) {

        messageLabel.setText(
                "❌ " + message
        );

        messageLabel.setStyle(
                "-fx-text-fill: #FF0000; "
                + "-fx-font-weight: bold;"
        );

        System.err.println(
                "[ERROR] " + message
        );
    }
}