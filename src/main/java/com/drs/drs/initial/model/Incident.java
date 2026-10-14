package com.drs.drs.initial.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Incident class represents a disaster incident in the DRS system.
 * Tracks incident lifecycle from creation through resolution.
 * 
 * @author DRS Development Team
 * @version 1.0
 */
public class Incident {

    // ========== Attributes ==========
    private String incidentId;
    private String disasterType;
    private String location;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    private int injuredCount;

    // ========== Constants ==========
    private static final String[] VALID_STATUSES = {
        "Reported", "Active", "Escalated", "Resolved", "Closed"
    };

    private static final String[] VALID_PRIORITIES = {
        "Low", "Medium", "High", "Critical"
    };

    // ========== Constructors ==========
    
    /**
     * Default constructor - initializes a new Incident
     */
    public Incident() {
        this.incidentId = "";
        this.disasterType = "";
        this.location = "";
        this.description = "";
        this.priority = "Medium";
        this.status = "Reported";
        this.reportedAt = LocalDateTime.now();
        this.resolvedAt = null;
        this.injuredCount = 0;
    }

    /**
     * Parametrized constructor - creates Incident with specified values
     * 
     * @param incidentId Unique incident identifier
     * @param disasterType Type of disaster
     * @param location Location of incident
     * @param description Description of situation
     * @param priority Priority level
     * @param status Current status
     * @param reportedAt Date/time of report
     * @param injuredCount Number of injured persons
     */
    public Incident(String incidentId, String disasterType, String location,
                    String description, String priority, String status,
                    LocalDateTime reportedAt, int injuredCount) {
        this.incidentId = incidentId;
        this.disasterType = disasterType;
        this.location = location;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.reportedAt = reportedAt;
        this.injuredCount = injuredCount;
        this.resolvedAt = null;
    }

    // ========== Getters ==========
    
    /**
     * Gets the unique incident ID
     * @return the incidentId
     */
    public String getIncidentId() {
        return incidentId;
    }

    /**
     * Gets the type of disaster
     * @return the disasterType
     */
    public String getDisasterType() {
        return disasterType;
    }

    /**
     * Gets the location of incident
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the priority level
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Gets the current status
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the report date/time
     * @return the reportedAt
     */
    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    /**
     * Gets the resolution date/time
     * @return the resolvedAt
     */
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    /**
     * Gets the number of injured
     * @return the injuredCount
     */
    public int getInjuredCount() {
        return injuredCount;
    }

    // ========== Setters ==========
    
    /**
     * Sets the incident ID
     * @param incidentId the incidentId to set
     */
    public void setIncidentId(String incidentId) {
        if (incidentId != null && !incidentId.isEmpty()) {
            this.incidentId = incidentId;
        }
    }

    /**
     * Sets the disaster type
     * @param disasterType the disasterType to set
     */
    public void setDisasterType(String disasterType) {
        if (disasterType != null && !disasterType.isEmpty()) {
            this.disasterType = disasterType;
        }
    }

    /**
     * Sets the location
     * @param location the location to set
     */
    public void setLocation(String location) {
        if (location != null && !location.isEmpty()) {
            this.location = location;
        }
    }

    /**
     * Sets the description
     * @param description the description to set
     */
    public void setDescription(String description) {
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
    }

    /**
     * Sets the priority level
     * @param priority the priority to set
     */
    public void setPriority(String priority) {
        if (isValidPriority(priority)) {
            this.priority = priority;
        }
    }

    /**
     * Sets the status
     * @param status the status to set
     */
    public void setStatus(String status) {
        if (isValidStatus(status)) {
            this.status = status;
        }
    }

    /**
     * Sets the report date/time
     * @param reportedAt the reportedAt to set
     */
    public void setReportedAt(LocalDateTime reportedAt) {
        if (reportedAt != null) {
            this.reportedAt = reportedAt;
        }
    }

    /**
     * Sets the injured count
     * @param injuredCount the injuredCount to set
     */
    public void setInjuredCount(int injuredCount) {
        if (injuredCount >= 0) {
            this.injuredCount = injuredCount;
        }
    }

    // ========== Validation Methods ==========
    
    /**
     * Validates if status is acceptable
     * @param status the status to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidStatus(String status) {
        if (status == null) return false;
        for (String valid : VALID_STATUSES) {
            if (valid.equals(status)) return true;
        }
        return false;
    }

    /**
     * Validates if priority is acceptable
     * @param priority the priority to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidPriority(String priority) {
        if (priority == null) return false;
        for (String valid : VALID_PRIORITIES) {
            if (valid.equals(priority)) return true;
        }
        return false;
    }

    // ========== Business Logic Methods ==========
    
    /**
     * Escalates incident to higher priority level
     * @return true if escalation was performed, false if already at highest priority
     */
    public boolean escalateIncident() {
        switch (this.priority) {
            case "Low":
                this.priority = "Medium";
                return true;
            case "Medium":
                this.priority = "High";
                return true;
            case "High":
                this.priority = "Critical";
                return true;
            case "Critical":
            default:
                return false; // Already at highest priority
        }
    }

    /**
     * Activates the incident and changes status to Active
     */
    public void activateIncident() {
        this.status = "Active";
    }

    /**
     * Resolves the incident and records resolution time
     */
    public void resolveIncident() {
        this.status = "Resolved";
        this.resolvedAt = LocalDateTime.now();
    }

    /**
     * Closes the incident
     */
    public void closeIncident() {
        this.status = "Closed";
        if (this.resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if incident is currently active
     * @return true if status is Active or Escalated, false otherwise
     */
    public boolean isActive() {
        return "Active".equals(this.status) || "Escalated".equals(this.status);
    }

    /**
     * Calculates minutes elapsed since incident was reported
     * @return number of minutes since reporting
     */
    public long getMinutesElapsed() {
        return java.time.temporal.ChronoUnit.MINUTES.between(this.reportedAt, LocalDateTime.now());
    }

    /**
     * Gets formatted date/time string for report time
     * @return formatted timestamp
     */
    public String getFormattedReportedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.reportedAt.format(formatter);
    }

    /**
     * Gets incident severity based on injured count
     * @return severity level (Low, Medium, High, Critical)
     */
    public String calculateSeverity() {
        if (injuredCount <= 10) return "Low";
        if (injuredCount <= 50) return "Medium";
        if (injuredCount <= 200) return "High";
        return "Critical";
    }

    /**
     * Gets a summary of the incident
     * @return formatted incident summary
     */
    public String getSummary() {
        return "Incident ID: " + incidentId + "\n" +
               "Disaster Type: " + disasterType + "\n" +
               "Location: " + location + "\n" +
               "Status: " + status + "\n" +
               "Priority: " + priority + "\n" +
               "Reported: " + getFormattedReportedAt() + "\n" +
               "Injured: " + injuredCount + "\n" +
               "Elapsed: " + getMinutesElapsed() + " minutes";
    }

    // ========== toString Method ==========
    
    /**
     * String representation of Incident object
     * @return formatted string with incident details
     */
    @Override
    public String toString() {
        return "Incident{" +
                "incidentId='" + incidentId + '\'' +
                ", disasterType='" + disasterType + '\'' +
                ", location='" + location + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", reported=" + getFormattedReportedAt() +
                ", injured=" + injuredCount +
                '}';
    }
    
    
    
}