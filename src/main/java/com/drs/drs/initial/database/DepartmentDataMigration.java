
package com.drs.drs.initial.database;

import com.drs.drs.initial.model.Department;
import com.drs.drs.initial.model.DepartmentRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DepartmentDataMigration {

    public static void migrateDepartments() {

        String sql = """
            INSERT INTO departments (
                department_id,
                name,
                type,
                contact_details,
                availability
            )
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                department_id = department_id
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            int inserted = 0;

            for (Department department :
                    DepartmentRepository.getDepartments()) {

                statement.setString(
                    1, department.getDepartmentId()
                );
                statement.setString(
                    2, department.getName()
                );
                statement.setString(
                    3, department.getType()
                );
                statement.setString(
                    4, department.getContactDetails()
                );
                statement.setString(
                    5, department.getAvailability()
                );

                int affected = statement.executeUpdate();

                if (affected == 1) {
                    inserted++;
                }
            }

            System.out.println(
                "Department migration completed. New records: "
                + inserted
            );

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException(
                "Department migration failed.", e
            );
        }
    }

    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();
        migrateDepartments();
    }
}
