
package com.drs.drs.initial.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {

        String createIncidentsTable = """
            CREATE TABLE IF NOT EXISTS incidents (
                incident_id VARCHAR(20) PRIMARY KEY,
                disaster_type VARCHAR(100) NOT NULL,
                location VARCHAR(255) NOT NULL,
                description TEXT,
                priority VARCHAR(30),
                status VARCHAR(30),
                reported_at DATETIME,
                resolved_at DATETIME NULL,
                injured_count INT DEFAULT 0
            )
            """;

        String createDepartmentsTable = """
            CREATE TABLE IF NOT EXISTS departments (
                department_id VARCHAR(20) PRIMARY KEY,
                name VARCHAR(150) NOT NULL,
                type VARCHAR(100),
                contact_details VARCHAR(255),
                availability VARCHAR(30) DEFAULT 'Available'
            )
            """;

        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                user_id VARCHAR(20) PRIMARY KEY,
                username VARCHAR(100) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL,
                full_name VARCHAR(150),
                email VARCHAR(255),
                role VARCHAR(30) DEFAULT 'Responder',
                department VARCHAR(150),
                is_active BOOLEAN DEFAULT TRUE
            )
            """;

        String createResponseTasksTable = """
            CREATE TABLE IF NOT EXISTS response_tasks (
                task_id VARCHAR(20) PRIMARY KEY,
                incident_id VARCHAR(20) NULL,
                description TEXT,
                status VARCHAR(30) DEFAULT 'Pending',
                assigned_department VARCHAR(150),
                created_at DATETIME,
                target_time DATETIME,
                completed_at DATETIME NULL,
                notes TEXT,
                CONSTRAINT fk_task_incident
                    FOREIGN KEY (incident_id)
                    REFERENCES incidents(incident_id)
                    ON DELETE SET NULL
            )
            """;

        String createAssessmentsTable = """
            CREATE TABLE IF NOT EXISTS assessments (
                assessment_id VARCHAR(20) PRIMARY KEY,
                incident_id VARCHAR(20) NULL,
                people_affected INT DEFAULT 0,
                impact_level VARCHAR(30),
                access_condition VARCHAR(50),
                assistance_needs TEXT,
                assessment_notes TEXT
            )
            """;

        String createDisasterReportsTable = """
            CREATE TABLE IF NOT EXISTS disaster_reports (
                disaster_report_id VARCHAR(20) PRIMARY KEY,
                reported_by VARCHAR(150),
                disaster_type VARCHAR(100),
                location VARCHAR(255),
                impact_level VARCHAR(30),
                assistance_needs TEXT,
                submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createAuditLogsTable = """
            CREATE TABLE IF NOT EXISTS audit_logs (
                log_id VARCHAR(20) PRIMARY KEY,
                action VARCHAR(50) NOT NULL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                details TEXT
            )
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             Statement statement =
                     connection.createStatement()) {

            statement.executeUpdate(createIncidentsTable);
            System.out.println("SUCCESS: Incidents table ready!");

            statement.executeUpdate(createDepartmentsTable);
            System.out.println("SUCCESS: Departments table ready!");

            statement.executeUpdate(createUsersTable);
            System.out.println("SUCCESS: Users table ready!");

            statement.executeUpdate(createResponseTasksTable);
            System.out.println("SUCCESS: Response tasks table ready!");

            statement.executeUpdate(createAssessmentsTable);
            System.out.println("SUCCESS: Assessments table ready!");

            statement.executeUpdate(createDisasterReportsTable);
            System.out.println("SUCCESS: Disaster reports table ready!");

            statement.executeUpdate(createAuditLogsTable);
            System.out.println("SUCCESS: Audit logs table ready!");

            System.out.println(
                "SUCCESS: Database initialization completed!"
            );

        } catch (SQLException e) {
            System.err.println(
                "ERROR: Database initialization failed."
            );
            e.printStackTrace();
            throw new IllegalStateException(
                "Could not initialize DRS database.", e
            );
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}
