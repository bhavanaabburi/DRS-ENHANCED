
package com.drs.drs.initial.model;

import com.drs.drs.initial.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ResponseTaskRepository {

    private static final ObservableList<ResponseTask> tasks =
            FXCollections.observableArrayList();

    static {
        loadTasks();
    }

    private ResponseTaskRepository() {
    }

    public static void loadTasks() {

        String sql = """
            SELECT *
            FROM response_tasks
            ORDER BY task_id
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            ObservableList<ResponseTask> loaded =
                    FXCollections.observableArrayList();

            while (resultSet.next()) {

                Timestamp created =
                        resultSet.getTimestamp("created_at");
                Timestamp target =
                        resultSet.getTimestamp("target_time");

                LocalDateTime createdAt = created == null
                        ? LocalDateTime.now()
                        : created.toLocalDateTime();

                LocalDateTime targetTime = target == null
                        ? LocalDateTime.now().plusHours(2)
                        : target.toLocalDateTime();

                ResponseTask task = new ResponseTask(
                        resultSet.getString("task_id"),
                        resultSet.getString("incident_id"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("assigned_department"),
                        createdAt,
                        targetTime,
                        resultSet.getString("notes")
                );

                loaded.add(task);
            }

            tasks.setAll(loaded);

            System.out.println(
                    "Loaded " + tasks.size()
                    + " response tasks from MySQL."
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to load response tasks.", e
            );
        }
    }

    public static ObservableList<ResponseTask> getTasks() {
        return tasks;
    }

    public static ResponseTask findById(String taskId) {

        if (taskId == null) {
            return null;
        }

        for (ResponseTask task : tasks) {
            if (task.getTaskId().equalsIgnoreCase(taskId.trim())) {
                return task;
            }
        }

        return null;
    }

    public static void addTask(ResponseTask task) {

        if (task == null) {
            return;
        }

        String sql = """
            INSERT INTO response_tasks (
                task_id, incident_id, description,
                status, assigned_department,
                created_at, target_time, completed_at, notes
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, task.getTaskId());

            String incidentId = task.getIncidentId();
            statement.setString(2,
                    incidentId == null || incidentId.isBlank()
                    ? null : incidentId);

            statement.setString(3, task.getDescription());
            statement.setString(4, task.getStatus());
            statement.setString(5, task.getAssignedDepartment());

            statement.setTimestamp(6,
                    toTimestamp(task.getCreatedAt()));
            statement.setTimestamp(7,
                    toTimestamp(task.getTargetTime()));
            statement.setTimestamp(8,
                    toTimestamp(task.getCompletedAt()));

            statement.setString(9, task.getNotes());

            statement.executeUpdate();
            tasks.add(task);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to save response task.", e
            );
        }
    }

    public static void updateTask(ResponseTask task) {

        if (task == null) {
            return;
        }

        String sql = """
            UPDATE response_tasks
            SET incident_id = ?,
                description = ?,
                status = ?,
                assigned_department = ?,
                created_at = ?,
                target_time = ?,
                completed_at = ?,
                notes = ?
            WHERE task_id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            String incidentId = task.getIncidentId();
            statement.setString(1,
                    incidentId == null || incidentId.isBlank()
                    ? null : incidentId);

            statement.setString(2, task.getDescription());
            statement.setString(3, task.getStatus());
            statement.setString(4, task.getAssignedDepartment());
            statement.setTimestamp(5,
                    toTimestamp(task.getCreatedAt()));
            statement.setTimestamp(6,
                    toTimestamp(task.getTargetTime()));
            statement.setTimestamp(7,
                    toTimestamp(task.getCompletedAt()));
            statement.setString(8, task.getNotes());
            statement.setString(9, task.getTaskId());

            int updated = statement.executeUpdate();

            if (updated != 1) {
                throw new IllegalStateException(
                        "Task not found in database: "
                        + task.getTaskId()
                );
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Failed to update response task.", e
            );
        }
    }

    private static Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    public static String generateTaskId() {

        int maxId = 0;

        for (ResponseTask task : tasks) {

            String id = task.getTaskId();

            if (id != null && id.matches("TASK-\\d+")) {
                int number = Integer.parseInt(id.substring(5));
                maxId = Math.max(maxId, number);
            }
        }

        return String.format("TASK-%03d", maxId + 1);
    }
}
