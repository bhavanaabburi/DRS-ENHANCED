package com.drs.drs.initial.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for Dashboard screen.
 * Main hub for navigating to all DRS features.
 * 
 * @author DRS Development Team
 * @version 1.0
 */
public class DashboardController {

    // ========== FXML Components ==========
    @FXML private Button reportDisasterButton;
    @FXML private Button assessPrioritiseButton;
    @FXML private Button manageDepartmentsButton;
    @FXML private Button viewIncidentsButton;
    @FXML private Button manageTodosButton;
    @FXML private Button logoutButton;
    @FXML private Button priorityCalculatorButton;
    @FXML private Button autoTaskGeneratorButton;
    
    @FXML private Label welcomeLabel;
    @FXML private Label systemStatusLabel;

    // ========== Initialize ==========
    
    /**
     * Initialize the controller when FXML is loaded
     */
    @FXML
    public void initialize() {
        System.out.println("[DashboardController] Initializing...");
        
        // Set welcome message
        welcomeLabel.setText("Welcome to DRS - Disaster Response System");
        systemStatusLabel.setText("System Status: OPERATIONAL ✓");
        
        // Setup button handlers
        setupButtonHandlers();
        String role = com.drs.drs.initial.server.DRSSession.role();
        if ("Responder".equals(role)) {
            assessPrioritiseButton.setDisable(true);
            manageDepartmentsButton.setDisable(true);
            autoTaskGeneratorButton.setDisable(true);
        }
        welcomeLabel.setText("Welcome to DRS - " + (role == null ? "User" : role));   
        
        System.out.println("[DashboardController] Initialization complete");
        
    }

    /**
     * Setup all button event handlers
     */
    private void setupButtonHandlers() {
        reportDisasterButton.setOnAction(event -> openReportDisaster());
        assessPrioritiseButton.setOnAction(event -> openAssessment());
        manageDepartmentsButton.setOnAction(event -> openDepartments());
        viewIncidentsButton.setOnAction(event -> openIncidents());
        manageTodosButton.setOnAction(event -> openTodos());
        priorityCalculatorButton.setOnAction(event -> openPriorityCalculator());
        autoTaskGeneratorButton.setOnAction(event -> openAutoTaskGenerator());
        logoutButton.setOnAction(event -> logout());
    }

    // ========== Navigation Methods ==========
    
    /**
     * Navigate to Report Disaster screen
     */
    @FXML
    private void openReportDisaster() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReportDisaster.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) reportDisasterButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("DRS - Report Disaster");
            System.out.println("[DashboardController] Navigated to Report Disaster");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Report Disaster: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Assessment screen
     */
    @FXML
    private void openAssessment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Assessment.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) assessPrioritiseButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("DRS - Assessment & Prioritization");
            System.out.println("[DashboardController] Navigated to Assessment");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Assessment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Departments screen
     */
    @FXML
    private void openDepartments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Departments.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) manageDepartmentsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("DRS - Manage Departments");
            System.out.println("[DashboardController] Navigated to Departments");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Departments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Incidents screen
     */
    @FXML
    private void openIncidents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Incidents.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) viewIncidentsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("DRS - View Incidents");
            System.out.println("[DashboardController] Navigated to Incidents");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Incidents: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigate to Tasks screen
     */
    @FXML
    private void openTodos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Tasks.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) manageTodosButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("DRS - Manage Response Tasks");
            System.out.println("[DashboardController] Navigated to Tasks");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
private void openPriorityCalculator() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PriorityAnalysis.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) reportDisasterButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("DRS - Priority Calculator");
        System.out.println("[DashboardController] Navigated to Priority Calculator");
    } catch (IOException e) {
        System.err.println("[ERROR] Failed to load Priority Calculator: " + e.getMessage());
        e.printStackTrace();
    }
}

@FXML
private void openAutoTaskGenerator() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AutoTaskGenerator.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) autoTaskGeneratorButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("DRS - Auto Task Generator");
        System.out.println("[DashboardController] Navigated to Auto Task Generator");
    } catch (IOException e) {
        System.err.println("[ERROR] Failed to load Auto Task Generator: " + e.getMessage());
        e.printStackTrace();
    }
}
    /**
     * Logout and return to Login screen
     */
    @FXML
    private void logout() {
        com.drs.drs.initial.server.DRSSession.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("DRS - Login");
            System.out.println("[DashboardController] User logged out");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load Login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}