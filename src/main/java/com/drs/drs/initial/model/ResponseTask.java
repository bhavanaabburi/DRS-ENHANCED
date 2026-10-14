package com.drs.drs.initial.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * ResponseTask represents a response activity assigned
 * to a department for a disaster incident.
 *
 * A task may be associated with an Incident using
 * incidentId and assigned to a responding department.
 *
 * @author DRS Development Team
 * @version 1.1
 */
public class ResponseTask {

    // ========== ATTRIBUTES ==========

    private String taskId;

    private String incidentId;

    private String description;

    private String status;

    private String assignedDepartment;

    private LocalDateTime createdAt;

    private LocalDateTime targetTime;

    private LocalDateTime completedAt;

    private String notes;


    // ========== CONSTANTS ==========

    private static final String[] VALID_STATUSES = {
        "Pending",
        "In Progress",
        "On Hold",
        "Completed",
        "Cancelled"
    };


    // ========== DEFAULT CONSTRUCTOR ==========

    public ResponseTask() {

        this.taskId = "";

        this.incidentId = "";

        this.description = "";

        this.status = "Pending";

        this.assignedDepartment = "";

        this.createdAt =
                LocalDateTime.now();

        this.targetTime =
                LocalDateTime.now()
                        .plusHours(2);

        this.completedAt = null;

        this.notes = "";
    }


    // ========== EXISTING CONSTRUCTOR ==========

    /**
     * Existing constructor retained for compatibility
     * with sample response tasks.
     */
    public ResponseTask(
            String taskId,
            String description,
            String status,
            String assignedDepartment,
            LocalDateTime createdAt,
            LocalDateTime targetTime,
            String notes) {

        this.taskId =
                taskId;

        this.incidentId =
                "";

        this.description =
                description;

        this.status =
                isValidStatus(status)
                ? status
                : "Pending";

        this.assignedDepartment =
                assignedDepartment;

        this.createdAt =
                createdAt != null
                ? createdAt
                : LocalDateTime.now();

        this.targetTime =
                targetTime != null
                ? targetTime
                : LocalDateTime.now()
                        .plusHours(2);

        this.notes =
                notes != null
                ? notes
                : "";

        this.completedAt =
                "Completed".equals(this.status)
                ? this.targetTime
                : null;
    }


    // ========== INCIDENT-AWARE CONSTRUCTOR ==========

    /**
     * Creates a response task associated with
     * a particular incident.
     */
    public ResponseTask(
            String taskId,
            String incidentId,
            String description,
            String status,
            String assignedDepartment,
            LocalDateTime createdAt,
            LocalDateTime targetTime,
            String notes) {

        this.taskId =
                taskId;

        this.incidentId =
                incidentId;

        this.description =
                description;

        this.status =
                isValidStatus(status)
                ? status
                : "Pending";

        this.assignedDepartment =
                assignedDepartment;

        this.createdAt =
                createdAt != null
                ? createdAt
                : LocalDateTime.now();

        this.targetTime =
                targetTime != null
                ? targetTime
                : LocalDateTime.now()
                        .plusHours(2);

        this.notes =
                notes != null
                ? notes
                : "";

        this.completedAt =
                "Completed".equals(this.status)
                ? this.targetTime
                : null;
    }


    // ========== GETTERS ==========

    public String getTaskId() {

        return taskId;
    }


    public String getIncidentId() {

        return incidentId;
    }


    public String getDescription() {

        return description;
    }


    public String getStatus() {

        return status;
    }


    public String getAssignedDepartment() {

        return assignedDepartment;
    }


    public LocalDateTime getCreatedAt() {

        return createdAt;
    }


    public LocalDateTime getTargetTime() {

        return targetTime;
    }


    public LocalDateTime getCompletedAt() {

        return completedAt;
    }


    public String getNotes() {

        return notes;
    }


    // ========== SETTERS ==========

    public void setTaskId(
            String taskId) {

        if (taskId != null
                && !taskId.trim().isEmpty()) {

            this.taskId =
                    taskId.trim();
        }
    }


    public void setIncidentId(
            String incidentId) {

        if (incidentId != null) {

            this.incidentId =
                    incidentId.trim();
        }
    }


    public void setDescription(
            String description) {

        if (description != null
                && !description.trim().isEmpty()) {

            this.description =
                    description.trim();
        }
    }


    public void setStatus(
            String status) {

        if (isValidStatus(status)) {

            this.status =
                    status;


            if ("Completed".equals(status)
                    && completedAt == null) {

                completedAt =
                        LocalDateTime.now();
            }
        }
    }


    public void setAssignedDepartment(
            String assignedDepartment) {

        if (assignedDepartment != null
                && !assignedDepartment
                        .trim()
                        .isEmpty()) {

            this.assignedDepartment =
                    assignedDepartment.trim();
        }
    }


    public void setCreatedAt(
            LocalDateTime createdAt) {

        if (createdAt != null) {

            this.createdAt =
                    createdAt;
        }
    }


    public void setTargetTime(
            LocalDateTime targetTime) {

        if (targetTime != null) {

            this.targetTime =
                    targetTime;
        }
    }


    public void setNotes(
            String notes) {

        if (notes != null) {

            this.notes =
                    notes.trim();
        }
    }


    // ========== VALIDATION ==========

    private boolean isValidStatus(
            String status) {

        if (status == null) {

            return false;
        }


        for (String valid
                : VALID_STATUSES) {

            if (valid.equals(status)) {

                return true;
            }
        }


        return false;
    }


    // ========== BUSINESS LOGIC ==========

    /**
     * Starts a pending task.
     */
    public boolean startTask() {

        if ("Pending".equals(
                this.status)) {

            this.status =
                    "In Progress";

            return true;
        }


        return false;
    }


    /**
     * Completes an active or held task.
     */
    public boolean completeTask() {

        if ("In Progress".equals(
                this.status)
                || "On Hold".equals(
                        this.status)) {

            this.status =
                    "Completed";

            this.completedAt =
                    LocalDateTime.now();

            return true;
        }


        return false;
    }


    /**
     * Cancels an unfinished task.
     */
    public boolean cancelTask() {

        if (!"Completed".equals(
                this.status)
                && !"Cancelled".equals(
                        this.status)) {

            this.status =
                    "Cancelled";

            return true;
        }


        return false;
    }


    /**
     * Places an active task on hold.
     */
    public boolean holdTask() {

        if ("In Progress".equals(
                this.status)) {

            this.status =
                    "On Hold";

            return true;
        }


        return false;
    }


    /**
     * Resumes a task that is currently on hold.
     */
    public boolean resumeTask() {

        if ("On Hold".equals(
                this.status)) {

            this.status =
                    "In Progress";

            return true;
        }


        return false;
    }


    // ========== DEADLINE LOGIC ==========

    public boolean isOverdue() {

        if ("Completed".equals(
                this.status)
                || "Cancelled".equals(
                        this.status)) {

            return false;
        }


        return LocalDateTime
                .now()
                .isAfter(
                        this.targetTime
                );
    }


    public long getMinutesUntilDeadline() {

        return ChronoUnit.MINUTES
                .between(
                        LocalDateTime.now(),
                        this.targetTime
                );
    }


    public String getFormattedTargetTime() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss"
                );


        return this.targetTime
                .format(
                        formatter
                );
    }


    // ========== TIME SPENT ==========

    public long getMinutesSpent() {

        if (this.completedAt == null) {

            return ChronoUnit.MINUTES
                    .between(
                            this.createdAt,
                            LocalDateTime.now()
                    );
        }


        return ChronoUnit.MINUTES
                .between(
                        this.createdAt,
                        this.completedAt
                );
    }


    // ========== PROGRESS ==========

    public String getProgressStatus() {

        switch (this.status) {

            case "Pending":

                return "Pending (0%)";


            case "In Progress":

                return "In Progress (50%)";


            case "On Hold":

                return "On Hold (Paused)";


            case "Completed":

                return "Completed (100%)";


            case "Cancelled":

                return "Cancelled (0%)";


            default:

                return this.status;
        }
    }


    // ========== INCIDENT ASSOCIATION ==========

    /**
     * Checks whether this task belongs to
     * a particular incident.
     */
    public boolean belongsToIncident(
            String incidentId) {

        if (incidentId == null
                || this.incidentId == null) {

            return false;
        }


        return this.incidentId
                .equalsIgnoreCase(
                        incidentId.trim()
                );
    }


    // ========== toString ==========

    @Override
    public String toString() {

        return "ResponseTask{"
                + "taskId='"
                + taskId
                + '\''
                + ", incidentId='"
                + incidentId
                + '\''
                + ", description='"
                + description
                + '\''
                + ", status='"
                + status
                + '\''
                + ", assignedDepartment='"
                + assignedDepartment
                + '\''
                + ", targetTime="
                + getFormattedTargetTime()
                + ", isOverdue="
                + isOverdue()
                + '}';
    }
}