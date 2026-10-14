package com.drs.drs.initial.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AuditLog class keeps a record of important actions in the DRS system.
 * Used for accountability and traceability of system activities.
 * 
 * Actions tracked: Create, Read, Update, Delete, Login, Logout, etc.
 * 
 * @author DRS Development Team
 * @version 1.0
 */
public class AuditLog {

    // ========== Attributes ==========
    private String logId;
    private String action;
    private LocalDateTime timestamp;
    private String details;

    // ========== Constants ==========
    private static final String[] VALID_ACTIONS = {
        "Create", "Read", "Update", "Delete", "Login", "Logout",
        "Report Submitted", "Assessment Created", "Task Assigned",
        "Status Changed", "Export", "Other"
    };

    // ========== Constructors ==========
    
    /**
     * Default constructor - initializes a new AuditLog
     */
    public AuditLog() {
        this.logId = "";
        this.action = "";
        this.timestamp = LocalDateTime.now();
        this.details = "";
    }

    /**
     * Parametrized constructor - creates AuditLog with specified values
     * 
     * @param logId Unique log entry identifier
     * @param action Action being logged (Create, Update, Delete, etc.)
     * @param timestamp Date and time of the action
     * @param details Additional details about the action
     */
    public AuditLog(String logId, String action,
                    LocalDateTime timestamp, String details) {
        this.logId = logId;
        this.action = action;
        this.timestamp = timestamp;
        this.details = details;
    }

    // ========== Getters ==========
    
    /**
     * Gets the unique log ID
     * @return the logId
     */
    public String getLogId() {
        return logId;
    }

    /**
     * Gets the action that was logged
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Gets the date and time of the logged action
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets additional details about the action
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    // ========== Setters ==========
    
    /**
     * Sets the unique log ID
     * @param logId the logId to set
     */
    public void setLogId(String logId) {
        if (logId != null && !logId.isEmpty()) {
            this.logId = logId;
        }
    }

    /**
     * Sets the action being logged
     * @param action the action to set
     */
    public void setAction(String action) {
        if (isValidAction(action)) {
            this.action = action;
        }
    }

    /**
     * Sets the date and time of the logged action
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        if (timestamp != null) {
            this.timestamp = timestamp;
        }
    }

    /**
     * Sets additional details about the action
     * @param details the details to set
     */
    public void setDetails(String details) {
        if (details != null && !details.isEmpty()) {
            this.details = details;
        }
    }

    // ========== Validation Methods ==========
    
    /**
     * Validates if the given action is one of the accepted actions
     * @param action the action to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidAction(String action) {
        if (action == null) return false;
        for (String validAction : VALID_ACTIONS) {
            if (validAction.equals(action)) return true;
        }
        return false;
    }

    // ========== Business Logic Methods ==========
    
    /**
     * Gets formatted string representation of timestamp
     * @return formatted date/time string
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.timestamp.format(formatter);
    }

    /**
     * Gets a summary of the log entry
     * @return formatted summary string
     */
    public String getSummary() {
        return "[" + getFormattedTimestamp() + "] " + action + ": " + details;
    }

    /**
     * Checks if this log entry is recent (within last hour)
     * @return true if logged within last 60 minutes, false otherwise
     */
    public boolean isRecent() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return this.timestamp.isAfter(oneHourAgo);
    }

    /**
     * Gets time elapsed since this action was logged
     * @return number of minutes since action was logged
     */
    public long getMinutesElapsed() {
        return java.time.temporal.ChronoUnit.MINUTES.between(this.timestamp, LocalDateTime.now());
    }

    // ========== toString Method ==========
    
    /**
     * String representation of AuditLog object
     * @return formatted string with all log details
     */
    @Override
    public String toString() {
        return "AuditLog{" +
                "logId='" + logId + '\'' +
                ", action='" + action + '\'' +
                ", timestamp=" + getFormattedTimestamp() +
                ", details='" + details + '\'' +
                '}';
    }
}