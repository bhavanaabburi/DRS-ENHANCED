
package com.drs.drs.initial.model;

import com.drs.drs.initial.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DepartmentRepository {

    private static final ObservableList<Department> departments =
            FXCollections.observableArrayList();

    static {
        loadDepartments();
    }

    private DepartmentRepository() {
    }

    /**
     * Loads all departments from MySQL.
     */
    public static void loadDepartments() {

        String sql = """
            SELECT department_id, name, type,
                   contact_details, availability
            FROM departments
            ORDER BY department_id
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            ObservableList<Department> loaded =
                    FXCollections.observableArrayList();

            while (resultSet.next()) {

                Department department = new Department(
                        resultSet.getString("department_id"),
                        resultSet.getString("name"),
                        resultSet.getString("type"),
                        resultSet.getString("contact_details"),
                        resultSet.getString("availability")
                );

                loaded.add(department);
            }

            departments.setAll(loaded);

            System.out.println(
                    "Loaded " + departments.size()
                    + " departments from MySQL."
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to load departments from MySQL.", e
            );
        }
    }

    /**
     * Returns the shared JavaFX department list.
     */
    public static ObservableList<Department> getDepartments() {
        return departments;
    }

    /**
     * Finds a department by ID.
     */
    public static Department findById(String departmentId) {

        if (departmentId == null) {
            return null;
        }

        for (Department department : departments) {

            if (department.getDepartmentId()
                    .equalsIgnoreCase(departmentId.trim())) {

                return department;
            }
        }

        return null;
    }

    /**
     * Finds the first department matching a type.
     */
    public static Department findByType(String type) {

        if (type == null) {
            return null;
        }

        for (Department department : departments) {

            if (department.getType()
                    .equalsIgnoreCase(type.trim())) {

                return department;
            }
        }

        return null;
    }

    /**
     * Updates department availability in MySQL
     * and then updates the JavaFX object.
     */
    public static void updateAvailability(
            String departmentId,
            String newAvailability) {

        if (newAvailability == null ||
                !(newAvailability.equals("Available") ||
                  newAvailability.equals("Busy") ||
                  newAvailability.equals("Unavailable"))) {

            throw new IllegalArgumentException(
                    "Invalid department availability."
            );
        }

        Department department = findById(departmentId);

        if (department == null) {
            throw new IllegalArgumentException(
                    "Department not found: " + departmentId
            );
        }

        String sql = """
            UPDATE departments
            SET availability = ?
            WHERE department_id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, newAvailability);
            statement.setString(2, departmentId);

            int updated = statement.executeUpdate();

            if (updated != 1) {
                throw new IllegalStateException(
                        "Department was not updated in MySQL."
                );
            }

            department.setAvailability(newAvailability);

            System.out.println(
                    "Updated department " + departmentId
                    + " to " + newAvailability
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to update department availability.", e
            );
        }
    }
}
