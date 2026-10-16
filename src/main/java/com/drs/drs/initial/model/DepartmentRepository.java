package com.drs.drs.initial.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DepartmentRepository {
    private static final ObservableList<Department> departments =
            FXCollections.observableArrayList();

    static { loadDepartments(); }

    private DepartmentRepository() { }

    private static Socket connect() throws java.io.IOException {
        Socket socket = com.drs.drs.initial.server.TLSConnection.open();
        socket.setSoTimeout(5000);
        return socket;
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static void loadDepartments() {
        try (Socket socket = connect();
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
             BufferedReader input = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            output.println(com.drs.drs.initial.server.DRSSession.wrap("GET_DEPARTMENTS_V1"));
            String header = input.readLine();
            if (header == null || !header.startsWith("OK ")) {
                throw new java.io.IOException("Server error: " + header);
            }
            int count = Integer.parseInt(header.substring(3));
            if (count < 0 || count > 10000) throw new java.io.IOException("Invalid row count");
            ObservableList<Department> loaded = FXCollections.observableArrayList();
            for (int i = 0; i < count; i++) {
                String line = input.readLine();
                if (line == null) throw new java.io.IOException("Incomplete server response");
                String[] fields = line.split("\t", -1);
                if (fields.length != 5) throw new java.io.IOException("Invalid department row");
                loaded.add(new Department(decode(fields[0]), decode(fields[1]),
                        decode(fields[2]), decode(fields[3]), decode(fields[4])));
            }
            departments.setAll(loaded);
            System.out.println("Loaded " + departments.size() + " departments through DRS server.");
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load departments through DRS server", e);
        }
    }

    public static ObservableList<Department> getDepartments() { return departments; }

    public static Department findById(String departmentId) {
        if (departmentId == null) return null;
        for (Department dept : departments) {
            if (dept.getDepartmentId().equalsIgnoreCase(departmentId.trim())) return dept;
        }
        return null;
    }

    public static Department findByType(String type) {
        if (type == null) return null;
        for (Department dept : departments) {
            if (dept.getType().equalsIgnoreCase(type.trim())) return dept;
        }
        return null;
    }

    public static void updateAvailability(String departmentId, String newAvailability) {
        Department dept = findById(departmentId);
        if (dept == null) throw new IllegalArgumentException("Department not found: " + departmentId);
        if (!java.util.List.of("Available", "Busy", "Unavailable").contains(newAvailability)) {
            throw new IllegalArgumentException("Invalid availability: " + newAvailability);
        }
        try (Socket socket = connect();
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
             BufferedReader input = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            output.println(com.drs.drs.initial.server.DRSSession.wrap("UPDATE_DEPARTMENT_V1\t" + encode(departmentId) + "\t" + encode(newAvailability)));
            String reply = input.readLine();
            if (!"OK".equals(reply)) throw new java.io.IOException("Server error: " + reply);
            dept.setAvailability(newAvailability);
        } catch (Exception e) {
            throw new IllegalStateException("Could not save department availability", e);
        }
    }
}
