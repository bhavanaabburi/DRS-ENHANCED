package com.drs.drs.initial.model;

/**
 * Department class represents an emergency response department or organization.
 * Departments coordinate with the DRS to deploy resources and handle response tasks.
 * 
 * Examples: Fire and Emergency, Hospital, Police, Transportation, etc.
 * Availability statuses: Available, Busy, Unavailable
 * 
 * @author DRS Development Team
 * @version 1.0
 */
public class Department {

    // ========== Attributes ==========
    private String departmentId;
    private String name;
    private String type;
    private String contactDetails;
    private String availability;

    // ========== Constants ==========
    private static final String[] VALID_AVAILABILITY = {
        "Available", "Busy", "Unavailable"
    };

    private static final String[] VALID_DEPARTMENT_TYPES = {
        "Fire and Emergency", "Hospital", "Police", "Transportation",
        "Waste Management", "Water Supply", "Schools", "Law Enforcement",
        "Utilities", "Search and Rescue", "Military", "Other"
    };

    // ========== Constructors ==========
    
    /**
     * Default constructor - initializes a new Department
     */
    public Department() {
        this.departmentId = "";
        this.name = "";
        this.type = "";
        this.contactDetails = "";
        this.availability = "Available";
    }

    /**
     * Parametrized constructor - creates Department with specified values
     * 
     * @param departmentId Unique department identifier
     * @param name Name of the department
     * @param type Type of department (Fire, Hospital, Police, etc.)
     * @param contactDetails Contact information (phone, email, etc.)
     * @param availability Current availability status
     */
    public Department(String departmentId, String name,
                      String type, String contactDetails,
                      String availability) {
        this.departmentId = departmentId;
        this.name = name;
        this.type = type;
        this.contactDetails = contactDetails;
        this.availability = availability;
    }

    // ========== Getters ==========
    
    /**
     * Gets the unique department ID
     * @return the departmentId
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * Gets the name of the department
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of department
     * @return the type (Fire, Hospital, Police, etc.)
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the contact details for the department
     * @return the contactDetails (phone, email, etc.)
     */
    public String getContactDetails() {
        return contactDetails;
    }

    /**
     * Gets the current availability status
     * @return the availability (Available, Busy, Unavailable)
     */
    public String getAvailability() {
        return availability;
    }

    // ========== Setters ==========
    
    /**
     * Sets the unique department ID
     * @param departmentId the departmentId to set
     */
    public void setDepartmentId(String departmentId) {
        if (departmentId != null && !departmentId.isEmpty()) {
            this.departmentId = departmentId;
        }
    }

    /**
     * Sets the name of the department
     * @param name the name to set
     */
    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    /**
     * Sets the type of department
     * @param type the type to set
     */
    public void setType(String type) {
        if (isValidDepartmentType(type)) {
            this.type = type;
        }
    }

    /**
     * Sets the contact details for the department
     * @param contactDetails the contactDetails to set
     */
    public void setContactDetails(String contactDetails) {
        if (contactDetails != null && !contactDetails.isEmpty()) {
            this.contactDetails = contactDetails;
        }
    }

    /**
     * Sets the availability status
     * Valid statuses: Available, Busy, Unavailable
     * 
     * @param availability the availability to set
     */
    public void setAvailability(String availability) {
        if (isValidAvailability(availability)) {
            this.availability = availability;
        }
    }

    // ========== Validation Methods ==========
    
    /**
     * Validates if the given availability status is acceptable
     * @param status the status to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidAvailability(String status) {
        if (status == null) return false;
        for (String valid : VALID_AVAILABILITY) {
            if (valid.equals(status)) return true;
        }
        return false;
    }

    /**
     * Validates if the given department type is acceptable
     * @param type the type to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidDepartmentType(String type) {
        if (type == null) return false;
        for (String valid : VALID_DEPARTMENT_TYPES) {
            if (valid.equals(type)) return true;
        }
        return false;
    }

    // ========== Business Logic Methods ==========
    
    /**
     * Updates department availability status
     * Only allows valid availability statuses
     * 
     * @param newAvailability the new availability status
     * @return true if status was updated, false if invalid
     */
    public boolean updateAvailability(String newAvailability) {
        if (isValidAvailability(newAvailability)) {
            this.availability = newAvailability;
            return true;
        }
        return false;
    }

    /**
     * Marks department as available for assignments
     */
    public void setAvailable() {
        this.availability = "Available";
    }

    /**
     * Marks department as busy (limited availability)
     */
    public void setBusy() {
        this.availability = "Busy";
    }

    /**
     * Marks department as unavailable
     */
    public void setUnavailable() {
        this.availability = "Unavailable";
    }

    /**
     * Checks if department is currently available for task assignment
     * @return true if availability is "Available", false otherwise
     */
    public boolean isAvailable() {
        return "Available".equals(this.availability);
    }

    /**
     * Checks if department is busy
     * @return true if availability is "Busy", false otherwise
     */
    public boolean isBusy() {
        return "Busy".equals(this.availability);
    }

    /**
     * Checks if department is unavailable
     * @return true if availability is "Unavailable", false otherwise
     */
    public boolean isUnavailable() {
        return "Unavailable".equals(this.availability);
    }

    /**
     * Gets a quick status summary of the department
     * @return formatted status string
     */
    public String getStatusSummary() {
        return name + " (" + type + ") - " + availability;
    }

    // ========== toString Method ==========
    
    /**
     * String representation of Department object
     * @return formatted string with all department details
     */
    @Override
    public String toString() {
        return "Department{" +
                "departmentId='" + departmentId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", availability='" + availability + '\'' +
                ", contact='" + contactDetails + '\'' +
                '}';
    }
}