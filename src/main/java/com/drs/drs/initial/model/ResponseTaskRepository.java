package com.drs.drs.initial.model;

import java.time.LocalDateTime;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Shared repository for response tasks.
 *
 * Stores response tasks so task status changes remain
 * available while navigating between screens during
 * the current application session.
 */
public class ResponseTaskRepository {

    private static final ObservableList<ResponseTask> tasks =
            FXCollections.observableArrayList();


    static {

        tasks.add(
                new ResponseTask(
                        "TASK-001",
                        "Evacuate residents from danger zone",
                        "Pending",
                        "Fire and Emergency",
                        LocalDateTime.now().minusHours(2),
                        LocalDateTime.now().plusHours(1),
                        "Priority: High"
                )
        );


        tasks.add(
                new ResponseTask(
                        "TASK-002",
                        "Fire suppression operations",
                        "In Progress",
                        "Fire and Emergency",
                        LocalDateTime.now().minusHours(1),
                        LocalDateTime.now().plusMinutes(30),
                        "Active firefighting"
                )
        );


        tasks.add(
                new ResponseTask(
                        "TASK-003",
                        "Rescue operations in progress",
                        "In Progress",
                        "Search and Rescue",
                        LocalDateTime.now().minusMinutes(45),
                        LocalDateTime.now().plusMinutes(45),
                        "Urgent rescue needed"
                )
        );


        tasks.add(
                new ResponseTask(
                        "TASK-004",
                        "Flood containment and barriers",
                        "Pending",
                        "Transportation Authority",
                        LocalDateTime.now().minusMinutes(30),
                        LocalDateTime.now().plusHours(2),
                        "Set up barriers"
                )
        );


        tasks.add(
                new ResponseTask(
                        "TASK-005",
                        "Emergency shelters setup",
                        "In Progress",
                        "Schools",
                        LocalDateTime.now().minusMinutes(20),
                        LocalDateTime.now().plusHours(3),
                        "Capacity: 500 people"
                )
        );


        tasks.add(
                new ResponseTask(
                        "TASK-006",
                        "Medical assistance deployment",
                        "Completed",
                        "Hospital",
                        LocalDateTime.now().minusHours(3),
                        LocalDateTime.now().minusHours(1),
                        "All teams deployed"
                )
        );


        tasks.add(
                new ResponseTask(
                        "TASK-007",
                        "Search and rescue coordination",
                        "On Hold",
                        "Search and Rescue",
                        LocalDateTime.now().minusHours(2),
                        LocalDateTime.now().plusHours(1),
                        "Waiting for weather clearance"
                )
        );


        tasks.add(
                new ResponseTask(
                        "TASK-008",
                        "Resource distribution to areas",
                        "Pending",
                        "Waste Management",
                        LocalDateTime.now().minusMinutes(15),
                        LocalDateTime.now().plusHours(4),
                        "Food and water supplies"
                )
        );
    }


    private ResponseTaskRepository() {
        // Prevent repository objects from being created
    }


    /**
     * Returns the shared response-task list.
     */
    public static ObservableList<ResponseTask> getTasks() {

        return tasks;
    }


    /**
     * Adds a new response task.
     */
    public static void addTask(
            ResponseTask task) {

        if (task != null) {
            tasks.add(task);
        }
    }


    /**
     * Finds a response task using its ID.
     */
    public static ResponseTask findById(
            String taskId) {

        if (taskId == null) {
            return null;
        }


        for (ResponseTask task : tasks) {

            if (task
                    .getTaskId()
                    .equalsIgnoreCase(
                            taskId.trim())) {

                return task;
            }
        }


        return null;
    }


    /**
     * Generates the next task ID.
     *
     * Example:
     * TASK-009
     */
    public static String generateTaskId() {

        return String.format(
                "TASK-%03d",
                tasks.size() + 1
        );
    }
}