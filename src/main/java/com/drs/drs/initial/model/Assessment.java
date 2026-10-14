package com.drs.drs.initial.model;

/**
 * Assessment class represents a detailed assessment of a disaster incident.
 * This is a critical class that determines resource needs, priority levels,
 * and response strategies for disaster incidents.
 * 
 * The Assessment calculates impact levels based on affected population,
 * determines priority, recommends required resources, and estimates response time.
 * 
 * @author DRS Development Team
 * @version 1.0
 */
public class Assessment {

    // ========== Attributes ==========
    private String assessmentId;
    private int peopleAffected;
    private String impactLevel;
    private String accessCondition;
    private String assistanceNeeds;
    private String assessmentNotes;

    // ========== Constants ==========
    private static final String[] VALID_IMPACT_LEVELS = {
        "Low", "Medium", "High", "Critical"
    };

    private static final String[] VALID_ACCESS_CONDITIONS = {
        "Accessible", "Partially Accessible", "Not Accessible"
    };

    // ========== Constructors ==========
    
    /**
     * Default constructor - initializes a new Assessment
     */
    public Assessment() {
        this.assessmentId = "";
        this.peopleAffected = 0;
        this.impactLevel = "Low";
        this.accessCondition = "Accessible";
        this.assistanceNeeds = "";
        this.assessmentNotes = "";
    }

    /**
     * Parametrized constructor - creates Assessment with specified values
     * 
     * @param assessmentId Unique assessment identifier
     * @param peopleAffected Number of people affected by the incident
     * @param impactLevel Level of impact (Low, Medium, High, Critical)
     * @param accessCondition Accessibility of affected area
     * @param assistanceNeeds Comma-separated list of required resources
     * @param assessmentNotes Additional assessment notes
     */
    public Assessment(String assessmentId, int peopleAffected,
                      String impactLevel, String accessCondition,
                      String assistanceNeeds, String assessmentNotes) {
        this.assessmentId = assessmentId;
        this.peopleAffected = peopleAffected;
        this.impactLevel = impactLevel;
        this.accessCondition = accessCondition;
        this.assistanceNeeds = assistanceNeeds;
        this.assessmentNotes = assessmentNotes;
    }

    // ========== Getters ==========
    
    /**
     * Gets the unique assessment ID
     * @return the assessmentId
     */
    public String getAssessmentId() {
        return assessmentId;
    }

    /**
     * Gets the number of people affected
     * @return the peopleAffected
     */
    public int getPeopleAffected() {
        return peopleAffected;
    }

    /**
     * Gets the impact level
     * @return the impactLevel (Low, Medium, High, Critical)
     */
    public String getImpactLevel() {
        return impactLevel;
    }

    /**
     * Gets the accessibility condition of the affected area
     * @return the accessCondition
     */
    public String getAccessCondition() {
        return accessCondition;
    }

    /**
     * Gets the required assistance needs
     * @return the assistanceNeeds (comma-separated)
     */
    public String getAssistanceNeeds() {
        return assistanceNeeds;
    }

    /**
     * Gets additional assessment notes
     * @return the assessmentNotes
     */
    public String getAssessmentNotes() {
        return assessmentNotes;
    }

    // ========== Setters ==========
    
    /**
     * Sets the unique assessment ID
     * @param assessmentId the assessmentId to set
     */
    public void setAssessmentId(String assessmentId) {
        if (assessmentId != null && !assessmentId.isEmpty()) {
            this.assessmentId = assessmentId;
        }
    }

    /**
     * Sets the number of people affected
     * @param peopleAffected the peopleAffected to set (must be >= 0)
     */
    public void setPeopleAffected(int peopleAffected) {
        if (peopleAffected >= 0) {
            this.peopleAffected = peopleAffected;
        }
    }

    /**
     * Sets the impact level
     * Valid levels: Low, Medium, High, Critical
     * 
     * @param impactLevel the impactLevel to set
     */
    public void setImpactLevel(String impactLevel) {
        if (isValidImpactLevel(impactLevel)) {
            this.impactLevel = impactLevel;
        }
    }

    /**
     * Sets the accessibility condition
     * Valid conditions: Accessible, Partially Accessible, Not Accessible
     * 
     * @param accessCondition the accessCondition to set
     */
    public void setAccessCondition(String accessCondition) {
        if (isValidAccessCondition(accessCondition)) {
            this.accessCondition = accessCondition;
        }
    }

    /**
     * Sets the required assistance needs
     * @param assistanceNeeds the assistanceNeeds to set
     */
    public void setAssistanceNeeds(String assistanceNeeds) {
        if (assistanceNeeds != null && !assistanceNeeds.isEmpty()) {
            this.assistanceNeeds = assistanceNeeds;
        }
    }

    /**
     * Sets additional assessment notes
     * @param assessmentNotes the assessmentNotes to set
     */
    public void setAssessmentNotes(String assessmentNotes) {
        if (assessmentNotes != null && !assessmentNotes.isEmpty()) {
            this.assessmentNotes = assessmentNotes;
        }
    }

    // ========== Validation Methods ==========
    
    /**
     * Validates if the given impact level is acceptable
     * @param impactLevel the impact level to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidImpactLevel(String impactLevel) {
        if (impactLevel == null) return false;
        for (String valid : VALID_IMPACT_LEVELS) {
            if (valid.equals(impactLevel)) return true;
        }
        return false;
    }

    /**
     * Validates if the given access condition is acceptable
     * @param accessCondition the access condition to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidAccessCondition(String accessCondition) {
        if (accessCondition == null) return false;
        for (String valid : VALID_ACCESS_CONDITIONS) {
            if (valid.equals(accessCondition)) return true;
        }
        return false;
    }

    // ========== CRITICAL CALCULATION METHODS FOR ASSESSMENT 2 ==========

    /**
     * REQUIREMENT: Calculates impact level based on affected population
     * 
     * Scale:
     * 0-100 people: Low
     * 101-500 people: Medium
     * 501-2000 people: High
     * 2001+ people: Critical
     * 
     * @return calculated impact level string
     */
    public String calculateImpactLevel() {
        if (peopleAffected <= 100) {
            return "Low";
        } else if (peopleAffected <= 500) {
            return "Medium";
        } else if (peopleAffected <= 2000) {
            return "High";
        } else {
            return "Critical";
        }
    }

    /**
     * REQUIREMENT: Calculates priority based on impact level and accessibility
     * 
     * Decision logic:
     * - Critical Impact: Always Critical Priority
     * - High Impact: High Priority (escalate to Critical if not accessible)
     * - Medium Impact: Medium Priority
     * - Low Impact: Low Priority
     * 
     * Accessibility modifier:
     * - Not Accessible: Increase priority by one level
     * - Partially Accessible: Normal priority
     * - Accessible: Normal priority
     * 
     * @return calculated priority level string
     */
    public String calculatePriority() {
        String basePriority;

        // Determine base priority from impact level
        switch (this.impactLevel) {
            case "Critical":
                basePriority = "Critical";
                break;
            case "High":
                basePriority = "High";
                break;
            case "Medium":
                basePriority = "Medium";
                break;
            case "Low":
            default:
                basePriority = "Low";
                break;
        }

        // Escalate priority if area is not accessible
        if ("Not Accessible".equals(this.accessCondition)) {
            switch (basePriority) {
                case "Low":
                    basePriority = "Medium";
                    break;
                case "Medium":
                    basePriority = "High";
                    break;
                case "High":
                    basePriority = "Critical";
                    break;
            }
        }

        return basePriority;
    }

    /**
     * REQUIREMENT: Recommends required resources based on assessment
     * 
     * Resource allocation rules:
     * - Always include: Emergency Personnel
     * - If peopleAffected > 100: Add Medical Teams
     * - If peopleAffected > 500: Add Shelter, Food, Water supplies
     * - If peopleAffected > 1000: Add Specialized Transport
     * - If Not Accessible: Add Rescue Equipment and Heavy Machinery
     * - If Critical Impact: Add Command Center and Coordination
     * 
     * @return comma-separated string of recommended resources
     */
    public String recommendResources() {
        StringBuilder resources = new StringBuilder();

        // Base resources - always needed
        resources.append("Emergency Personnel");

        // Add medical if significant population affected
        if (peopleAffected > 100) {
            resources.append(", Medical Teams");
        }

        // Add shelter and supplies if large population affected
        if (peopleAffected > 500) {
            resources.append(", Shelter Services");
            resources.append(", Food Supplies");
            resources.append(", Water Supplies");
        }

        // Add specialized transport for very large populations
        if (peopleAffected > 1000) {
            resources.append(", Specialized Transport");
            resources.append(", Logistics Support");
        }

        // Add rescue equipment if area is not accessible
        if ("Not Accessible".equals(this.accessCondition)) {
            resources.append(", Rescue Equipment");
            resources.append(", Heavy Machinery");
            resources.append(", Specialized Training");
        }

        // Add coordination if partial access
        if ("Partially Accessible".equals(this.accessCondition) && peopleAffected > 1000) {
            resources.append(", Coordination Center");
        }

        // Add command center for critical impact
        if ("Critical".equals(this.impactLevel)) {
            resources.append(", Command Center");
            resources.append(", Communication Equipment");
        }

        return resources.toString();
    }

    /**
     * REQUIREMENT: Calculates estimated response time based on impact and accessibility
     * 
     * Base response times:
     * - Accessible: 2 hours
     * - Partially Accessible: 8 hours
     * - Not Accessible: 18 hours
     * 
     * Modifiers:
     * - Critical Impact: Halve response time (rush response)
     * - High Impact: Standard time
     * - Medium/Low Impact: Standard time
     * 
     * @return estimated response time in hours
     */
    public int calculateEstimatedResponseTime() {
        int responseTime = 2; // Default

        // Base response time based on accessibility
        switch (this.accessCondition) {
            case "Not Accessible":
                responseTime = 18; // Need specialized equipment
                break;
            case "Partially Accessible":
                responseTime = 8;  // Some routes available
                break;
            case "Accessible":
            default:
                responseTime = 2;  // Direct access
                break;
        }

        // Reduce response time for critical situations
        if ("Critical".equals(this.impactLevel)) {
            responseTime = Math.max(1, responseTime / 2); // Halve time, minimum 1 hour
        }

        return responseTime;
    }

    /**
     * Generates a comprehensive assessment summary report
     * Useful for displaying assessment details to users
     * 
     * @return formatted assessment summary string
     */
    public String generateSummary() {
        return "ASSESSMENT SUMMARY\n" +
                "==================\n" +
                "Assessment ID: " + assessmentId + "\n" +
                "People Affected: " + peopleAffected + "\n" +
                "Impact Level: " + impactLevel + "\n" +
                "Priority: " + calculatePriority() + "\n" +
                "Accessibility: " + accessCondition + "\n" +
                "Est. Response Time: " + calculateEstimatedResponseTime() + " hours\n" +
                "Required Resources: " + recommendResources() + "\n" +
                "Notes: " + assessmentNotes;
    }

    /**
     * Gets array of required resources (parsed from comma-separated string)
     * @return array of individual resource needs
     */
    public String[] getResourcesArray() {
        String resources = recommendResources();
        if (resources == null || resources.isEmpty()) {
            return new String[0];
        }
        return resources.split(",\\s*");
    }

    // ========== toString Method ==========
    
    /**
     * String representation of Assessment object
     * @return formatted string with all assessment details
     */
    @Override
    public String toString() {
        return "Assessment{" +
                "assessmentId='" + assessmentId + '\'' +
                ", peopleAffected=" + peopleAffected +
                ", impactLevel='" + impactLevel + '\'' +
                ", priority='" + calculatePriority() + '\'' +
                ", accessCondition='" + accessCondition + '\'' +
                ", responseTime=" + calculateEstimatedResponseTime() + " hours" +
                '}';
    }
}