package com.drs.drs.initial.model;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ResponseTaskRepository {
    private static final ObservableList<ResponseTask> tasks = FXCollections.observableArrayList();
    static { loadTasks(); }
    private ResponseTaskRepository() {}

    private static String enc(String value) {
        return Base64.getEncoder().encodeToString((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
    }
    private static String dec(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
    private static LocalDateTime date(String value) {
        if (value == null || value.isBlank()) return null;
        return java.sql.Timestamp.valueOf(value).toLocalDateTime();
    }
    private static String dateText(LocalDateTime date) {
        return date == null ? "" : java.sql.Timestamp.valueOf(date).toString();
    }
    private static Socket connect() throws IOException {
        Socket socket = com.drs.drs.initial.server.TLSConnection.open();
        socket.setSoTimeout(8000);
        return socket;
    }
    public static void loadTasks() {
        try (Socket socket = connect();
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            output.println(com.drs.drs.initial.server.DRSSession.wrap("GET_TASKS_V1"));
            String header = input.readLine();
            if (header == null || !header.startsWith("OK ")) throw new IOException("Server: " + header);
            int count = Integer.parseInt(header.substring(3).trim());
            if (count < 0 || count > 100000) throw new IOException("Invalid task count");
            ObservableList<ResponseTask> loaded = FXCollections.observableArrayList();
            for (int i=0;i<count;i++) {
                String row = input.readLine();
                if (row == null) throw new IOException("Incomplete task response");
                String[] p = row.split("\t", -1);
                if (p.length != 9) throw new IOException("Invalid task record");
                String[] f = new String[9];
                for (int j=0;j<9;j++) f[j] = dec(p[j]);
                ResponseTask task = new ResponseTask(f[0], f[1], f[2], f[3], f[4],
                        date(f[5]), date(f[6]), f[8]);
                // Completed tasks must retain their original completion time.
                // ResponseTask has no completedAt setter; add it below.
                task.setCompletedAt(date(f[7]));
                loaded.add(task);
            }
            tasks.setAll(loaded);
        } catch (Exception e) { throw new IllegalStateException("Failed to load tasks through DRS Server",e); }
    }
    public static ObservableList<ResponseTask> getTasks() { return tasks; }
    public static ResponseTask findById(String id) {
        if (id == null) return null;
        for (ResponseTask task : tasks) if (id.trim().equalsIgnoreCase(task.getTaskId())) return task;
        return null;
    }
    private static void command(String command) {
        try (Socket socket = connect();
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            output.println(com.drs.drs.initial.server.DRSSession.wrap(command));
            String reply = input.readLine();
            if (!"OK".equals(reply)) throw new IOException("Server: " + reply);
        } catch (IOException e) { throw new IllegalStateException("Task operation failed",e); }
    }
    public static void addTask(ResponseTask task) {
        if (task == null) return;
        if (!"Pending".equals(task.getStatus())) throw new IllegalArgumentException("New tasks must be Pending");
        String[] f = {task.getTaskId(),task.getIncidentId(),task.getDescription(),task.getStatus(),
                task.getAssignedDepartment(),dateText(task.getCreatedAt()),dateText(task.getTargetTime()),
                dateText(task.getCompletedAt()),task.getNotes()};
        StringBuilder cmd = new StringBuilder("ADD_TASK_V1");
        for (String field : f) cmd.append('\t').append(enc(field));
        command(cmd.toString());
        loadTasks();
    }
    public static void updateTask(ResponseTask task) {
        if (task == null) return;
        updateStatus(task.getTaskId(), task.getStatus());
    }
    public static void updateStatus(String taskId, String status) {
        command("UPDATE_TASK_STATUS_V1\t" + enc(taskId) + "\t" + enc(status));
        loadTasks();
    }
    public static String generateTaskId() {
        int max=0;
        for (ResponseTask task:tasks) {
            if (task.getTaskId()!=null && task.getTaskId().matches("TASK-\\d+"))
                max=Math.max(max,Integer.parseInt(task.getTaskId().substring(5)));
        }
        return String.format("TASK-%03d",max+1);
    }
}
