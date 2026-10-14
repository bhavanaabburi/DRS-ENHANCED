
package com.drs.drs.initial.database;

import com.drs.drs.initial.model.ResponseTask;
import com.drs.drs.initial.model.ResponseTaskRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ResponseTaskDataMigration {

    public static void migrateTasks() {

        String sql = """
            INSERT INTO response_tasks (
                task_id,
                incident_id,
                description,
                status,
                assigned_department,
                created_at,
                target_time,
                completed_at,
                notes
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                task_id = task_id
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            int inserted = 0;

            for (ResponseTask task :
                    ResponseTaskRepository.getTasks()) {

                statement.setString(1, task.getTaskId());

                String incidentId = task.getIncidentId();
                statement.setString(
                    2,
                    incidentId == null || incidentId.isBlank()
                        ? null : incidentId
                );

                statement.setString(3, task.getDescription());
                statement.setString(4, task.getStatus());
                statement.setString(
                    5, task.getAssignedDepartment()
                );

                statement.setTimestamp(
                    6,
                    task.getCreatedAt() == null
                        ? null
                        : Timestamp.valueOf(task.getCreatedAt())
                );

                statement.setTimestamp(
                    7,
                    task.getTargetTime() == null
                        ? null
                        : Timestamp.valueOf(task.getTargetTime())
                );

                statement.setTimestamp(
                    8,
                    task.getCompletedAt() == null
                        ? null
                        : Timestamp.valueOf(task.getCompletedAt())
                );

                statement.setString(9, task.getNotes());

                int affected = statement.executeUpdate();

                if (affected == 1) {
                    inserted++;
                }
            }

            System.out.println(
                "Response task migration completed. New records: "
                + inserted
            );

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException(
                "Response task migration failed.", e
            );
        }
    }

    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();
        migrateTasks();
    }
}
