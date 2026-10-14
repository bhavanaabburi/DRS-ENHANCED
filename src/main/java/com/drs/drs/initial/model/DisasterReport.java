package com.drs.drs.initial.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DisasterReport class represents a disaster report submitted to the DRS.
 * Captures initial information about a disaster event.
 * 
 * Disaster types: Hurricane, Fire, Earthquake, Flood, Tornado, Tsunami, 
 *                 Landslide, Wildfire, Winter Storm, Other
 * Impact levels: Minor, Moderate, Severe, Critical
 * 
 * @author DRS Development Team
 * @version 1.0
 */
public class DisasterReport {

    // ========== Attributes ==========
    private String disasterReportId;
    private String reportedBy;
    private String disasterType;
    private String location;
    private String impactLevel;
    private String assistanceNeeds;
    private LocalDateTime submittedAt;

    // ========== Constants ==========
    private static final String[] VALID_DISASTER_TYPES = {
        "Hurricane", "Fire", "Earthquake", "Flood", "Tornado",
        "Tsunami", "Landslide", "Wildfire", "Winter Storm", "Other"
    };

    private static final String[] VALID_IMPACT_LEVELS = {
        "Minor", "Moderate", "Severe", "Critical"
    };

    // ========== Constructors ==========
    
    /**
     * Default constructor - initializes a new DisasterReport
     */
    public DisasterReport() {
        this.disasterReportId = "";
        this.reportedBy = "";
        this.disasterType = "";
        this.location = "";
        this.impactLevel = "";
        this.assistanceNeeds = "";
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * Parametrized constructor - creates DisasterReport with specified values
     * 
     * @param disasterReportId Unique report identifier
     * @param reportedBy Name/ID of person reporting
     * @param disasterType Type of disaster (Hurricane, Fire, etc.)
     * @param location Location of disaster
     * @param impactLevel Impact level (Minor, Moderate, Severe, Critical)
     * @param assistanceNeeds Description of assistance needs
     */
    public DisasterReport(String disasterReportId, String reportedBy,
                         String disasterType, String location,
                         String impactLevel, String assistanceNeeds) {
        this.disasterReportId = disasterReportId;
        this.reportedBy = reportedBy;
        this.disasterType = disasterType;
        this.location = location;
        this.impactLevel = impactLevel;
        this.assistanceNeeds = assistanceNeeds;
        this.submittedAt = LocalDateTime.now();
    }

    /**
     * Parametrized constructor with timestamp
     * 
     * @param disasterReportId Unique report identifier
     * @param reportedBy Name/ID of person reporting
     * @param disasterType Type of disaster
     * @param location Location of disaster
     * @param impactLevel Impact level
     * @param assistanceNeeds Description of assistance needs
     * @param submittedAt Submission timestamp
     */
    public DisasterReport(String disasterReportId, String reportedBy,
                         String disasterType, String location,
                         String impactLevel, String assistanceNeeds,
                         LocalDateTime submittedAt) {
        this.disasterReportId = disasterReportId;
        this.reportedBy = reportedBy;
        this.disasterType = disasterType;
        this.location = location;
        this.impactLevel = impactLevel;
        this.assistanceNeeds = assistanceNeeds;
        this.submittedAt = submittedAt;
    }

    // ========== Getters ==========
    
    /**
     * Gets the unique disaster report ID
     * @return the disasterReportId
     */
    public String getDisasterReportId() {
        return disasterReportId;
    }

    /**
     * Gets the name/ID of person who reported the disaster
     * @return the reportedBy
     */
    public String getReportedBy() {
        return reportedBy;
    }

    /**
     * Gets the type of disaster
     * @return the disasterType
     */
    public String getDisasterType() {
        return disasterType;
    }

    /**
     * Gets the location of the disaster
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the impact level of the disaster
     * @return the impactLevel (Minor, Moderate, Severe, Critical)
     */
    public String getImpactLevel() {
        return impactLevel;
    }

    /**
     * Gets the assistance needs description
     * @return the assistanceNeeds
     */
    public String getAssistanceNeeds() {
        return assistanceNeeds;
    }

    /**
     * Gets the submission timestamp
     * @return the submittedAt
     */
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    // ========== Setters ==========
    
    /**
     * Sets the unique disaster report ID
     * @param disasterReportId the disasterReportId to set
     */
    public void setDisasterReportId(String disasterReportId) {
        if (disasterReportId != null && !disasterReportId.isEmpty()) {
            this.disasterReportId = disasterReportId;
        }
    }

    /**
     * Sets who reported the disaster
     * @param reportedBy the reportedBy to set
     */
    public void setReportedBy(String reportedBy) {
        if (reportedBy != null && !reportedBy.isEmpty()) {
            this.reportedBy = reportedBy;
        }
    }

    /**
     * Sets the type of disaster
     * @param disasterType the disasterType to set
     */
    public void setDisasterType(String disasterType) {
        if (isValidDisasterType(disasterType)) {
            this.disasterType = disasterType;
        }
    }

    /**
     * Sets the location of the disaster
     * @param location the location to set
     */
    public void setLocation(String location) {
        if (location != null && !location.isEmpty()) {
            this.location = location;
        }
    }

    /**
     * Sets the impact level
     * @param impactLevel the impactLevel to set
     */
    public void setImpactLevel(String impactLevel) {
        if (isValidImpactLevel(impactLevel)) {
            this.impactLevel = impactLevel;
        }
    }

    /**
     * Sets the assistance needs description
     * @param assistanceNeeds the assistanceNeeds to set
     */
    public void setAssistanceNeeds(String assistanceNeeds) {
        if (assistanceNeeds != null && !assistanceNeeds.isEmpty()) {
            this.assistanceNeeds = assistanceNeeds;
        }
    }

    /**
     * Sets the submission timestamp
     * @param submittedAt the submittedAt to set
     */
    public void setSubmittedAt(LocalDateTime submittedAt) {
        if (submittedAt != null) {
            this.submittedAt = submittedAt;
        }
    }

    // ========== Validation Methods ==========
    
    /**
     * Validates if the given disaster type is acceptable
     * @param type the type to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidDisasterType(String type) {
        if (type == null) return false;
        for (String validType : VALID_DISASTER_TYPES) {
            if (validType.equals(type)) return true;
        }
        return false;
    }

    /**
     * Validates if the given impact level is acceptable
     * @param level the level to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidImpactLevel(String level) {
        if (level == null) return false;
        for (String validLevel : VALID_IMPACT_LEVELS) {
            if (validLevel.equals(level)) return true;
        }
        return false;
    }

    // ========== Business Logic Methods ==========
    
    /**
     * Gets time elapsed since this report was submitted
     * @return number of minutes since submission
     */
    public long getMinutesSinceSubmission() {
        return java.time.temporal.ChronoUnit.MINUTES.between(this.submittedAt, LocalDateTime.now());
    }

    /**
     * Gets formatted string representation of submission time
     * @return formatted date/time string
     */
    public String getFormattedSubmittedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return this.submittedAt.format(formatter);
    }

    /**
     * Determines if this disaster report requires immediate attention
     * Critical or Severe disasters always require immediate attention
     * 
     * @return true if report requires immediate attention, false otherwise
     */
    public boolean requiresImmediateAttention() {
        return "Critical".equals(this.impactLevel) || "Severe".equals(this.impactLevel);
    }

    /**
     * Converts assistance needs string into array
     * Splits by common delimiters: comma, newline, semicolon
     * 
     * @return array of individual assistance needs
     */
    public String[] getAssistanceNeedsArray() {
        if (this.assistanceNeeds == null || this.assistanceNeeds.isEmpty()) {
            return new String[0];
        }

        // Split by multiple delimiters
        String[] needs = this.assistanceNeeds
            .split("[,;\\n]");

        // Trim whitespace from each need
        for (int i = 0; i < needs.length; i++) {
            needs[i] = needs[i].trim();
        }

        return needs;
    }

    // ========== toString Method ==========
    
    /**
     * String representation of DisasterReport object
     * @return formatted string with all report details
     */
    @Override
    public String toString() {
        return "DisasterReport{" +
                "disasterReportId='" + disasterReportId + '\'' +
                ", reportedBy='" + reportedBy + '\'' +
                ", disasterType='" + disasterType + '\'' +
                ", location='" + location + '\'' +
                ", impactLevel='" + impactLevel + '\'' +
                ", submittedAt=" + getFormattedSubmittedAt() +
                '}';
    }
}