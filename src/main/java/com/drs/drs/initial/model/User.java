package com.drs.drs.initial.model;

/**
 * User class represents a system user in the DRS application.
 * Users can have different roles: Admin, Coordinator, or Responder
 * Each role has different permissions and responsibilities.
 * 
 * @author DRS Development Team
 * @version 1.0
 */
public class User {

    // ========== Attributes ==========
    private String userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
    private String department;
    private boolean isActive;

    // ========== Constants ==========
    private static final String[] VALID_ROLES = {
        "Admin", "Coordinator", "Responder"
    };

    // ========== Constructors ==========
    
    /**
     * Default constructor - initializes a new User
     */
    public User() {
        this.userId = "";
        this.username = "";
        this.password = "";
        this.fullName = "";
        this.email = "";
        this.role = "Responder";
        this.department = "";
        this.isActive = true;
    }

    /**
     * Parametrized constructor - creates User with specified values
     * 
     * @param userId Unique user identifier
     * @param username Login username
     * @param password User password (should be hashed in production)
     * @param fullName Full name of the user
     * @param email Email address
     * @param role User role (Admin, Coordinator, Responder)
     * @param department Department assignment
     * @param isActive Whether user account is active
     */
    public User(String userId, String username, String password, String fullName,
                String email, String role, String department, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.department = department;
        this.isActive = isActive;
    }

    // ========== Getters ==========
    
    /**
     * Gets the unique user ID
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the login username
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the user password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the full name of the user
     * @return the fullName
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets the email address
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user role
     * @return the role (Admin, Coordinator, Responder)
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the department assignment
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Checks if the user account is active
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    // ========== Setters ==========
    
    /**
     * Sets the unique user ID
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        if (userId != null && !userId.isEmpty()) {
            this.userId = userId;
        }
    }

    /**
     * Sets the login username
     * @param username the username to set
     */
    public void setUsername(String username) {
        if (username != null && !username.isEmpty()) {
            this.username = username;
        }
    }

    /**
     * Sets the user password
     * In production, this should hash the password
     * @param password the password to set
     */
    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }

    /**
     * Sets the full name of the user
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
            this.fullName = fullName;
        }
    }

    /**
     * Sets the email address
     * @param email the email to set
     */
    public void setEmail(String email) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
    }

    /**
     * Sets the user role
     * Valid roles: Admin, Coordinator, Responder
     * @param role the role to set
     */
    public void setRole(String role) {
        if (isValidRole(role)) {
            this.role = role;
        }
    }

    /**
     * Sets the department assignment
     * @param department the department to set
     */
    public void setDepartment(String department) {
        if (department != null && !department.isEmpty()) {
            this.department = department;
        }
    }

    /**
     * Sets the active status of the user account
     * @param isActive true to activate, false to deactivate
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    // ========== Validation Methods ==========
    
    /**
     * Validates if the given role is acceptable
     * @param role the role to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidRole(String role) {
        if (role == null) return false;
        for (String validRole : VALID_ROLES) {
            if (validRole.equals(role)) return true;
        }
        return false;
    }

    // ========== Business Logic Methods ==========
    
    /**
     * Authenticates the user with provided credentials
     * @param providedPassword the password to verify
     * @return true if password matches and user is active, false otherwise
     */
    public boolean authenticate(String providedPassword) {
        if (!isActive) {
            return false;
        }
        if (providedPassword == null) {
            return false;
        }
        return this.password.equals(providedPassword);
    }

    /**
     * Checks if user has Admin role
     * @return true if role is Admin, false otherwise
     */
    public boolean isAdmin() {
        return "Admin".equals(this.role);
    }

    /**
     * Checks if user has Coordinator role
     * @return true if role is Coordinator, false otherwise
     */
    public boolean isCoordinator() {
        return "Coordinator".equals(this.role);
    }

    /**
     * Checks if user has Responder role
     * @return true if role is Responder, false otherwise
     */
    public boolean isResponder() {
        return "Responder".equals(this.role);
    }

    /**
     * Checks if user can access admin features
     * @return true if user is Admin, false otherwise
     */
    public boolean hasAdminAccess() {
        return isAdmin() && isActive;
    }

    /**
     * Checks if user can create and assign tasks
     * @return true if user is Coordinator or Admin, false otherwise
     */
    public boolean canAssignTasks() {
        return (isCoordinator() || isAdmin()) && isActive;
    }

    /**
     * Checks if user can respond to incidents
     * @return true if user is Responder, Coordinator, or Admin, false otherwise
     */
    public boolean canRespond() {
        return (isResponder() || isCoordinator() || isAdmin()) && isActive;
    }

    /**
     * Gets a summary of user's permissions
     * @return formatted permissions string
     */
    public String getPermissionsSummary() {
        StringBuilder permissions = new StringBuilder();
        permissions.append("User: ").append(fullName).append(" (").append(role).append(")\n");
        permissions.append("Permissions: ");
        
        if (canRespond()) permissions.append("Respond ");
        if (canAssignTasks()) permissions.append("AssignTasks ");
        if (hasAdminAccess()) permissions.append("Admin");
        
        return permissions.toString();
    }

    // ========== toString Method ==========
    
    /**
     * String representation of User object
     * @return formatted string with user details
     */
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}