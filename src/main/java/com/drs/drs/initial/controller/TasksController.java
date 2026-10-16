package com.drs.drs.initial.controller;

import com.drs.drs.initial.model.ResponseTask;
import com.drs.drs.initial.model.ResponseTaskRepository;

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
 * Controller for Manage Response Tasks.
 *
 * Uses ResponseTask business methods to manage
 * the lifecycle of response tasks.
 *
 * @author DRS Development Team
 * @version 1.2
 */
public class TasksController {

    // ========== FXML COMPONENTS ==========

    @FXML
    private VBox tasksList;

    @FXML
    private Label messageLabel;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<String> filterComboBox;


    // ========== DATA ==========

    private ObservableList<ResponseTask> tasks;


    // ========== INITIALIZE ==========

    @FXML
    public void initialize() {

        System.out.println(
                "[TasksController] Initializing..."
        );

        setupFilterComboBox();

        initializeTasks();

        displayTasks();

        setupEventHandlers();

        System.out.println(
                "[TasksController] Initialization complete"
        );
    }


    // ========== LOAD TASKS ==========

    private void initializeTasks() {

        tasks =
                ResponseTaskRepository
                        .getTasks();

        System.out.println(
                "[TasksController] Loaded "
                + tasks.size()
                + " tasks from repository"
        );
    }


    // ========== FILTER ==========

    // ========== CREATE UNASSIGNED TASK ==========

    @FXML
    private void createUnassignedTask() {
        if (!java.util.List.of("Admin", "Coordinator").contains(
                com.drs.drs.initial.server.DRSSession.role())) {
            messageLabel.setText("Only Admin or Coordinator can create response tasks.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Response Task");
        dialog.setHeaderText("Create a pending task for automatic department assignment");
        dialog.setContentText("Task description:");
        java.util.Optional<String> answer = dialog.showAndWait();
        if (answer.isEmpty()) return;
        String description = answer.get().trim();
        if (description.isBlank() || description.length() > 250) {
            messageLabel.setText("Enter a description between 1 and 250 characters.");
            return;
        }
        try {
            ResponseTaskRepository.loadTasks();
            ResponseTask task = new ResponseTask(
                    ResponseTaskRepository.generateTaskId(),
                    description, "Pending", "Unassigned",
                    java.time.LocalDateTime.now(),
                    java.time.LocalDateTime.now().plusHours(2),
                    "Awaiting automatic department assignment");
            ResponseTaskRepository.addTask(task);
            tasks = ResponseTaskRepository.getTasks();
            filterComboBox.setValue("All Tasks");
            displayTasks();
            messageLabel.setText("Created " + task.getTaskId() + ". Click Auto-Assign Department on its card.");
        } catch (Exception ex) {
            messageLabel.setText("Could not create task: " + ex.getMessage());
        }
    }

    private void setupFilterComboBox() {

        filterComboBox.setItems(
                FXCollections.observableArrayList(
                        "All Tasks",
                        "Pending",
                        "In Progress",
                        "Completed",
                        "On Hold",
                        "Cancelled"
                )
        );

        filterComboBox.setValue(
                "All Tasks"
        );

        filterComboBox.setOnAction(
                event -> filterTasks()
        );
    }


    // ========== EVENT HANDLERS ==========

    private void setupEventHandlers() {

        if (backButton != null) {

            backButton.setOnAction(
                    event -> openDashboard()
            );
        }
    }


    // ========== DISPLAY ==========

    private void displayTasks() {

        displayTaskList(
                tasks,
                "Total Tasks: "
                + tasks.size()
        );
    }


    // ========== TASK CARD ==========

    private VBox createTaskCard(
            ResponseTask task) {

        VBox card =
                new VBox(10);


        card.setStyle(
                "-fx-border-color: "
                + getStatusBorderColor(
                        task.getStatus())
                + "; "
                + "-fx-border-width: 2; "
                + "-fx-border-radius: 5; "
                + "-fx-padding: 15; "
                + "-fx-background-color: "
                + getStatusBackgroundColor(
                        task.getStatus())
                + "; "
                + "-fx-effect: dropshadow("
                + "gaussian, rgba(0,0,0,0.1), "
                + "5, 0, 0, 2);"
        );


        // ---------- Description ----------

        Label titleLabel =
                new Label(
                        task.getDescription()
                );

        titleLabel.setStyle(
                "-fx-font-size: 14px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: #1F5B99;"
        );


        // ---------- Task ID ----------

        Label detailsLabel =
                new Label(
                        "ID: "
                        + task.getTaskId()
                        + " | Department: "
                        + task.getAssignedDepartment()
                );

        detailsLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #666666;"
        );


        // ---------- Incident ----------

        String incidentId =
                task.getIncidentId();


        Label incidentLabel =
                new Label();


        if (incidentId != null
                && !incidentId
                        .trim()
                        .isEmpty()) {

            incidentLabel.setText(
                    "Incident: "
                    + incidentId
            );

            incidentLabel.setStyle(
                    "-fx-font-size: 11px; "
                    + "-fx-font-weight: bold; "
                    + "-fx-text-fill: #D32F2F;"
            );

        } else {

            incidentLabel.setText(
                    "Incident: General response task"
            );

            incidentLabel.setStyle(
                    "-fx-font-size: 11px; "
                    + "-fx-text-fill: #999999;"
            );
        }


        // ---------- Status ----------

        Label statusLabel =
                new Label(
                        "Status: "
                        + task.getStatus()
                );

        statusLabel.setStyle(
                "-fx-font-size: 12px; "
                + "-fx-font-weight: bold; "
                + "-fx-text-fill: "
                + getStatusTextColor(
                        task.getStatus())
                + ";"
        );


        // ---------- Time ----------

        Label timeLabel =
                new Label(
                        "Time: "
                        + task.getMinutesSpent()
                        + " minutes spent"
                );

        timeLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #999999;"
        );


        // ---------- Deadline ----------

        Label deadlineLabel =
                new Label(
                        createDeadlineText(task)
                );

        if (task.isOverdue()) {

            deadlineLabel.setStyle(
                    "-fx-font-size: 11px; "
                    + "-fx-font-weight: bold; "
                    + "-fx-text-fill: #DC3545;"
            );

        } else {

            deadlineLabel.setStyle(
                    "-fx-font-size: 11px; "
                    + "-fx-text-fill: #666666;"
            );
        }


        // ---------- Progress ----------

        Label progressLabel =
                new Label(
                        "Progress: "
                        + task.getProgressStatus()
                );

        progressLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #555555;"
        );


        // ---------- Notes ----------

        Label notesLabel =
                new Label(
                        "Notes: "
                        + (
                            task.getNotes() == null
                            || task.getNotes()
                                    .isBlank()
                            ? "None"
                            : task.getNotes()
                        )
                );

        notesLabel.setStyle(
                "-fx-font-size: 11px; "
                + "-fx-text-fill: #555555;"
        );

        notesLabel.setWrapText(
                true
        );


        // ---------- Buttons ----------

        HBox buttonBox =
                createActionButtons(
                        task
                );


        // ---------- Build Card ----------

        card.getChildren().addAll(
                titleLabel,
                detailsLabel,
                incidentLabel,
                statusLabel,
                timeLabel,
                deadlineLabel,
                progressLabel,
                notesLabel,
                new Separator()
        );


        if (!buttonBox
                .getChildren()
                .isEmpty()) {

            card.getChildren().add(
                    buttonBox
            );
        }


        if ("Pending".equals(task.getStatus()) &&
                (task.getAssignedDepartment() == null || task.getAssignedDepartment().isBlank() ||
                 java.util.List.of("Unassigned", "Not Assigned", "None").contains(task.getAssignedDepartment()))) {
            Button assign = new Button("Auto-Assign Department");
            assign.setDisable(!java.util.List.of("Admin", "Coordinator").contains(
                    com.drs.drs.initial.server.DRSSession.role()));
            assign.setOnAction(event -> {
                try {
                    String department = com.drs.drs.initial.server.DRSOperationsClient.assignDepartment(task.getTaskId());
                    ResponseTaskRepository.loadTasks();
                    tasks = ResponseTaskRepository.getTasks();
                    filterComboBox.setValue("All Tasks");
                    displayTasks();
                    messageLabel.setText("Assigned " + task.getTaskId() + " to " + department);
                } catch (Exception ex) {
                    messageLabel.setText(ex.getMessage());
                }
            });
            card.getChildren().add(assign);
        }

        return card;
    }


    // ========== ACTION BUTTONS ==========

    private HBox createActionButtons(
            ResponseTask task) {

        HBox buttonBox =
                new HBox(10);


        switch (task.getStatus()) {

            case "Pending":

                Button startButton =
                        createButton(
                                "Start",
                                "#4CAF50",
                                "white"
                        );


                startButton.setOnAction(
                        event ->
                                startTask(task)
                );


                buttonBox
                        .getChildren()
                        .add(startButton);

                break;


            case "In Progress":

                Button completeButton =
                        createButton(
                                "Complete",
                                "#28A745",
                                "white"
                        );


                completeButton.setOnAction(
                        event ->
                                completeTask(task)
                );


                Button holdButton =
                        createButton(
                                "Hold",
                                "#FFC107",
                                "black"
                        );


                holdButton.setOnAction(
                        event ->
                                holdTask(task)
                );


                buttonBox
                        .getChildren()
                        .addAll(
                                completeButton,
                                holdButton
                        );

                break;


            case "On Hold":

                Button resumeButton =
                        createButton(
                                "Resume",
                                "#2196F3",
                                "white"
                        );


                resumeButton.setOnAction(
                        event ->
                                resumeTask(task)
                );


                Button completeHeldButton =
                        createButton(
                                "Complete",
                                "#28A745",
                                "white"
                        );


                completeHeldButton.setOnAction(
                        event ->
                                completeTask(task)
                );


                buttonBox
                        .getChildren()
                        .addAll(
                                resumeButton,
                                completeHeldButton
                        );

                break;


            default:

                break;
        }


        if (!"Completed".equals(
                task.getStatus())
                && !"Cancelled".equals(
                        task.getStatus())) {

            Button cancelButton =
                    createButton(
                            "Cancel",
                            "#DC3545",
                            "white"
                    );


            cancelButton.setOnAction(
                    event ->
                            cancelTask(task)
            );


            buttonBox
                    .getChildren()
                    .add(cancelButton);
        }


        return buttonBox;
    }


    // ========== BUTTON CREATION ==========

    private Button createButton(
            String text,
            String background,
            String textColour) {

        Button button =
                new Button(text);


        button.setStyle(
                "-fx-padding: 8px 15px; "
                + "-fx-font-size: 11px; "
                + "-fx-background-color: "
                + background
                + "; "
                + "-fx-text-fill: "
                + textColour
                + "; "
                + "-fx-cursor: hand;"
        );


        return button;
    }


    // ========== MODEL BUSINESS OPERATIONS ==========

    private void startTask(
            ResponseTask task) {

        if (task.startTask()) {

            taskOperationSuccessful(
                    task,
                    "started"
            );

        } else {

            showTaskError(
                    task,
                    "cannot be started from status "
                    + task.getStatus()
            );
        }
    }


    private void completeTask(
            ResponseTask task) {

        if (task.completeTask()) {

            taskOperationSuccessful(
                    task,
                    "completed"
            );

        } else {

            showTaskError(
                    task,
                    "cannot be completed from status "
                    + task.getStatus()
            );
        }
    }


    private void holdTask(
            ResponseTask task) {

        if (task.holdTask()) {

            taskOperationSuccessful(
                    task,
                    "placed on hold"
            );

        } else {

            showTaskError(
                    task,
                    "cannot be placed on hold from status "
                    + task.getStatus()
            );
        }
    }


    private void resumeTask(
            ResponseTask task) {

        if (task.resumeTask()) {

            taskOperationSuccessful(
                    task,
                    "resumed"
            );

        } else {

            showTaskError(
                    task,
                    "cannot be resumed from status "
                    + task.getStatus()
            );
        }
    }


    private void cancelTask(
            ResponseTask task) {

        if (task.cancelTask()) {

            taskOperationSuccessful(
                    task,
                    "cancelled"
            );

        } else {

            showTaskError(
                    task,
                    "cannot be cancelled from status "
                    + task.getStatus()
            );
        }
    }


    // ========== OPERATION RESULT ==========

    private void taskOperationSuccessful(
            ResponseTask task,
            String action) {
        try {
            ResponseTaskRepository.updateStatus(task.getTaskId(), task.getStatus());
            tasks = ResponseTaskRepository.getTasks();
        } catch (RuntimeException e) {
            try { ResponseTaskRepository.loadTasks(); } catch (RuntimeException ignored) { }
            tasks = ResponseTaskRepository.getTasks();
            messageLabel.setText("Task update failed: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #DC3545; -fx-font-weight: bold;");
            filterTasks();
            return;
        }


        System.out.println(
                "[TASK UPDATED] "
                + task.getTaskId()
                + " → "
                + task.getStatus()
        );


        messageLabel.setText(
                "✅ "
                + task.getTaskId()
                + " "
                + action
                + " successfully."
        );


        messageLabel.setStyle(
                "-fx-text-fill: #28A745; "
                + "-fx-font-weight: bold;"
        );


        refreshCurrentView();
    }


    private void showTaskError(
            ResponseTask task,
            String reason) {

        messageLabel.setText(
                "❌ "
                + task.getTaskId()
                + " "
                + reason
        );


        messageLabel.setStyle(
                "-fx-text-fill: #DC3545; "
                + "-fx-font-weight: bold;"
        );
    }


    // ========== DEADLINE TEXT ==========

    private String createDeadlineText(
            ResponseTask task) {

        if ("Completed".equals(
                task.getStatus())) {

            return "Target Time: "
                    + task.getFormattedTargetTime()
                    + " | Completed";
        }


        if ("Cancelled".equals(
                task.getStatus())) {

            return "Target Time: "
                    + task.getFormattedTargetTime()
                    + " | Cancelled";
        }


        long remaining =
                task.getMinutesUntilDeadline();


        if (remaining < 0) {

            return "Deadline: OVERDUE by "
                    + Math.abs(remaining)
                    + " minutes";
        }


        return "Deadline: "
                + remaining
                + " minutes remaining";
    }


    // ========== FILTERING ==========

    @FXML
    private void filterTasks() {

        String selectedFilter =
                filterComboBox.getValue();


        if ("All Tasks".equals(
                selectedFilter)) {

            displayTasks();

            return;
        }


        ObservableList<ResponseTask> filtered =
                FXCollections
                        .observableArrayList();


        for (ResponseTask task : tasks) {

            if (task.getStatus()
                    .equals(selectedFilter)) {

                filtered.add(task);
            }
        }


        if (filtered.isEmpty()) {

            tasksList
                    .getChildren()
                    .clear();


            Label noResultsLabel =
                    new Label(
                            "No tasks found for: "
                            + selectedFilter
                    );


            noResultsLabel.setStyle(
                    "-fx-font-size: 14px; "
                    + "-fx-text-fill: #999999;"
            );


            tasksList
                    .getChildren()
                    .add(noResultsLabel);


            messageLabel.setText(
                    "No tasks matching: "
                    + selectedFilter
            );


            messageLabel.setStyle(
                    "-fx-text-fill: #999999;"
            );


            return;
        }


        displayTaskList(
                filtered,
                "Found "
                + filtered.size()
                + " task(s)"
        );
    }


    private void refreshCurrentView() {

        filterTasks();
    }


    // ========== DISPLAY LIST ==========

    private void displayTaskList(
            ObservableList<ResponseTask> list,
            String message) {

        tasksList
                .getChildren()
                .clear();


        if (list == null
                || list.isEmpty()) {

            Label noDataLabel =
                    new Label(
                            "No tasks found"
                    );


            noDataLabel.setStyle(
                    "-fx-font-size: 14px; "
                    + "-fx-text-fill: #999999;"
            );


            tasksList
                    .getChildren()
                    .add(noDataLabel);

            return;
        }


        for (ResponseTask task : list) {

            tasksList
                    .getChildren()
                    .add(
                            createTaskCard(task)
                    );
        }


        messageLabel.setText(
                message
        );


        messageLabel.setStyle(
                "-fx-text-fill: #9C27B0; "
                + "-fx-font-weight: bold;"
        );
    }


    // ========== COLOURS ==========

    private String getStatusBorderColor(
            String status) {

        switch (status) {

            case "Completed":
                return "#28A745";

            case "In Progress":
                return "#2196F3";

            case "On Hold":
                return "#FF9800";

            case "Cancelled":
                return "#DC3545";

            case "Pending":
                return "#9C27B0";

            default:
                return "#CCCCCC";
        }
    }


    private String getStatusBackgroundColor(
            String status) {

        switch (status) {

            case "Completed":
                return "#E8F5E9";

            case "In Progress":
                return "#E3F2FD";

            case "On Hold":
                return "#FFF3E0";

            case "Cancelled":
                return "#FFEBEE";

            case "Pending":
                return "#F3E5F5";

            default:
                return "#FFFFFF";
        }
    }


    private String getStatusTextColor(
            String status) {

        switch (status) {

            case "Completed":
                return "#28A745";

            case "In Progress":
                return "#2196F3";

            case "On Hold":
                return "#FF9800";

            case "Cancelled":
                return "#DC3545";

            case "Pending":
                return "#9C27B0";

            default:
                return "#333333";
        }
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

            System.err.println(
                    "[ERROR] Failed to load Dashboard: "
                    + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}