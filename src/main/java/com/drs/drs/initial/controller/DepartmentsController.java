package com.drs.drs.initial.controller;

import com.drs.drs.initial.model.Department;
import com.drs.drs.initial.model.DepartmentRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for Departments screen.
 *
 * Manages:
 * - Department display
 * - Department availability
 * - Searching
 * - Filtering
 * - Department coordination
 *
 * @author DRS Development Team
 * @version 1.1
 */
public class DepartmentsController {

    // ========== FXML COMPONENTS ==========

    @FXML
    private VBox departmentsList;

    @FXML
    private Label messageLabel;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;


    // ========== DATA ==========

    private ObservableList<Department> departments;


    // ========== INITIALIZE ==========

    @FXML
    public void initialize() {

        System.out.println(
                "[DepartmentsController] Initializing..."
        );

        setupFilterComboBox();

        initializeDepartments();

        displayDepartments();

        setupEventHandlers();

        System.out.println(
                "[DepartmentsController] Initialization complete"
        );
    }


    // ========== LOAD DEPARTMENTS ==========

    /**
     * Loads departments from the shared repository.
     */
    private void initializeDepartments() {

        departments =
                DepartmentRepository
                        .getDepartments();

        System.out.println(
                "[DepartmentsController] Loaded "
                + departments.size()
                + " departments from repository"
        );
    }


    // ========== FILTER SETUP ==========

    private void setupFilterComboBox() {

        filterComboBox.setItems(
                FXCollections.observableArrayList(
                        "All Departments",
                        "Available",
                        "Busy",
                        "Unavailable"
                )
        );

        filterComboBox.setValue(
                "All Departments"
        );

        filterComboBox.setOnAction(
                event -> applyFilters()
        );
    }


    // ========== EVENT HANDLERS ==========

    private void setupEventHandlers() {

        backButton.setOnAction(
                event -> goBackToDashboard()
        );

        searchButton.setOnAction(
                event -> applyFilters()
        );

        /*
         * Pressing Enter inside the search field
         * will also perform the search.
         */
        searchField.setOnAction(
                event -> applyFilters()
        );
    }


    // ========== DISPLAY ALL ==========

    private void displayDepartments() {

        displayDepartmentList(
                departments,
                "✅ Displaying "
                + departments.size()
                + " departments"
        );
    }


    // ========== CREATE DEPARTMENT CARD ==========

    private VBox createDepartmentCard(
            Department dept) {

        VBox card =
                new VBox(10);


        card.setStyle(
                "-fx-border-color: #0066CC; "
                + "-fx-border-width: 2; "
                + "-fx-border-radius: 5; "
                + "-fx-padding: 15; "
                + "-fx-background-color: #FFFFFF; "
                + "-fx-effect: dropshadow("
                + "gaussian, rgba(0,0,0,0.1), "
                + "5, 0, 0, 2);"
        );


        // ========== HEADER ==========

        HBox headerBox =
                new HBox(10);


        Label nameLabel =
                new Label(
                        dept.getName()
                );

        nameLabel.setStyle(
                "-fx-font-size: 14px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: #1F5B99;"
        );


        Label typeLabel =
                new Label(
                        "("
                        + dept.getType()
                        + ")"
                );

        typeLabel.setStyle(
                "-fx-font-size: 12px; "
                + "-fx-text-fill: #666666;"
        );


        headerBox
                .getChildren()
                .addAll(
                        nameLabel,
                        typeLabel
                );


        // ========== DEPARTMENT ID ==========

        Label idLabel =
                new Label(
                        "ID: "
                        + dept.getDepartmentId()
                );

        idLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #999999;"
        );


        // ========== CONTACT DETAILS ==========

        Label contactLabel =
                new Label(
                        dept.getContactDetails()
                );

        contactLabel.setStyle(
                "-fx-font-size: 12px; "
                + "-fx-text-fill: #555555;"
        );

        contactLabel.setWrapText(
                true
        );


        // ========== STATUS ==========

        HBox statusBox =
                new HBox(10);


        Label statusTitleLabel =
                new Label(
                        "Status:"
                );

        statusTitleLabel.setStyle(
                "-fx-font-weight: bold; "
                + "-fx-font-size: 12px;"
        );


        Label statusValueLabel =
                new Label(
                        dept.getAvailability()
                );

        statusValueLabel.setStyle(
                getStatusStyle(
                        dept.getAvailability()
                )
        );


        statusBox
                .getChildren()
                .addAll(
                        statusTitleLabel,
                        statusValueLabel
                );


        // ========== STATUS BUTTONS ==========

        HBox buttonBox =
                new HBox(10);


        Button availableBtn =
                new Button(
                        "✓ Available"
                );

        availableBtn.setStyle(
                "-fx-padding: 8px 15px; "
                + "-fx-font-size: 11px; "
                + "-fx-background-color: #28A745; "
                + "-fx-text-fill: white; "
                + "-fx-cursor: hand;"
        );


        availableBtn.setOnAction(
                event ->
                        updateDepartmentStatus(
                                dept,
                                "Available"
                        )
        );


        Button busyBtn =
                new Button(
                        "⚙ Busy"
                );

        busyBtn.setStyle(
                "-fx-padding: 8px 15px; "
                + "-fx-font-size: 11px; "
                + "-fx-background-color: #FFC107; "
                + "-fx-text-fill: black; "
                + "-fx-cursor: hand;"
        );


        busyBtn.setOnAction(
                event ->
                        updateDepartmentStatus(
                                dept,
                                "Busy"
                        )
        );


        Button unavailableBtn =
                new Button(
                        "✗ Unavailable"
                );

        unavailableBtn.setStyle(
                "-fx-padding: 8px 15px; "
                + "-fx-font-size: 11px; "
                + "-fx-background-color: #DC3545; "
                + "-fx-text-fill: white; "
                + "-fx-cursor: hand;"
        );


        unavailableBtn.setOnAction(
                event ->
                        updateDepartmentStatus(
                                dept,
                                "Unavailable"
                        )
        );


        buttonBox
                .getChildren()
                .addAll(
                        availableBtn,
                        busyBtn,
                        unavailableBtn
                );


        // ========== SUMMARY ==========

        Label summaryLabel =
                new Label(
                        dept.getStatusSummary()
                );

        summaryLabel.setStyle(
                "-fx-font-size: 12px; "
                + "-fx-text-fill: #1F5B99; "
                + "-fx-font-weight: bold;"
        );


        // ========== ADD TO CARD ==========

        card
                .getChildren()
                .addAll(
                        headerBox,
                        idLabel,
                        contactLabel,
                        statusBox,
                        buttonBox,
                        new Separator(),
                        summaryLabel
                );


        return card;
    }


    // ========== STATUS STYLE ==========

    private String getStatusStyle(
            String status) {

        switch (status) {

            case "Available":

                return "-fx-text-fill: #28A745; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 13px;";


            case "Busy":

                return "-fx-text-fill: #FF9800; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 13px;";


            case "Unavailable":

                return "-fx-text-fill: #DC3545; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 13px;";


            default:

                return "-fx-text-fill: #666666; "
                        + "-fx-font-size: 12px;";
        }
    }


    // ========== UPDATE STATUS ==========

    /**
     * Updates the actual Department object stored
     * inside DepartmentRepository.
     */
    private void updateDepartmentStatus(
            Department dept,
            String newStatus) {

        if (dept.updateAvailability(
                newStatus)) {

            System.out.println(
                    "[DEPARTMENT STATUS UPDATED] "
                    + dept.getName()
                    + " → "
                    + newStatus
            );


            messageLabel.setText(
                    "✅ "
                    + dept.getName()
                    + " status updated to: "
                    + newStatus
            );

            messageLabel.setStyle(
                    "-fx-text-fill: #28A745; "
                    + "-fx-font-weight: bold;"
            );


            /*
             * Redraw the list so the status label
             * and summary both display the new value.
             */
            applyFilters();
        }
    }


    // ========== FILTER AND SEARCH ==========

    /**
     * Applies both status filtering and text
     * searching at the same time.
     */
    @FXML
    private void applyFilters() {

        String selectedStatus =
                filterComboBox
                        .getValue();


        String searchTerm =
                searchField
                        .getText()
                        .trim()
                        .toLowerCase();


        ObservableList<Department> filtered =
                FXCollections
                        .observableArrayList();


        for (Department dept : departments) {

            // ----- Status match -----

            boolean statusMatches =
                    "All Departments"
                            .equals(selectedStatus)
                    || dept
                            .getAvailability()
                            .equals(selectedStatus);


            // ----- Search match -----

            boolean searchMatches =
                    searchTerm.isEmpty()
                    || dept
                            .getName()
                            .toLowerCase()
                            .contains(searchTerm)
                    || dept
                            .getType()
                            .toLowerCase()
                            .contains(searchTerm)
                    || dept
                            .getDepartmentId()
                            .toLowerCase()
                            .contains(searchTerm);


            if (statusMatches
                    && searchMatches) {

                filtered.add(
                        dept
                );
            }
        }


        // ========== DISPLAY RESULTS ==========

        if (filtered.isEmpty()) {

            departmentsList
                    .getChildren()
                    .clear();


            Label noResultsLabel =
                    new Label(
                            "No departments found"
                    );

            noResultsLabel.setStyle(
                    "-fx-font-size: 14px; "
                    + "-fx-text-fill: #999999;"
            );


            departmentsList
                    .getChildren()
                    .add(
                            noResultsLabel
                    );


            messageLabel.setText(
                    "❌ No departments match "
                    + "the selected criteria."
            );

            messageLabel.setStyle(
                    "-fx-text-fill: #DC3545; "
                    + "-fx-font-weight: bold;"
            );

            return;
        }


        displayDepartmentList(
                filtered,
                "✅ Found "
                + filtered.size()
                + " department(s)"
        );
    }


    // ========== DISPLAY A LIST ==========

    private void displayDepartmentList(
            ObservableList<Department> list,
            String message) {

        departmentsList
                .getChildren()
                .clear();


        if (list == null
                || list.isEmpty()) {

            Label noDataLabel =
                    new Label(
                            "No departments found"
                    );

            noDataLabel.setStyle(
                    "-fx-font-size: 14px; "
                    + "-fx-text-fill: #999999;"
            );


            departmentsList
                    .getChildren()
                    .add(
                            noDataLabel
                    );

            return;
        }


        for (Department dept : list) {

            departmentsList
                    .getChildren()
                    .add(
                            createDepartmentCard(
                                    dept
                            )
                    );
        }


        messageLabel.setText(
                message
        );

        messageLabel.setStyle(
                "-fx-text-fill: #28A745; "
                + "-fx-font-weight: bold;"
        );
    }


    // ========== NAVIGATION ==========

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
                    "[DepartmentsController] "
                    + "Navigated back to Dashboard"
            );


        } catch (IOException e) {

            System.err.println(
                    "[ERROR] Failed to load Dashboard: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}