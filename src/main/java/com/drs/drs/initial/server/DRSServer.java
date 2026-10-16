package com.drs.drs.initial.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.drs.drs.initial.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.io.Console;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

public class DRSServer {

    private static final int PORT = 5050;
    private static final java.util.concurrent.ConcurrentHashMap<String, Session> SESSIONS = new java.util.concurrent.ConcurrentHashMap<>();
    private record Session(String username, String role, long expires) {}
    private static String hash(char[] password, byte[] salt) throws Exception {
        var spec = new javax.crypto.spec.PBEKeySpec(password, salt, 210000, 256);
        try {
            byte[] key = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return java.util.Base64.getEncoder().encodeToString(key);
        } finally { spec.clearPassword(); }
    }
    private static void audit(String action, String detail) {
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(
                "INSERT INTO audit_logs(log_id,action,details) VALUES(?,?,?)")) {
            ps.setString(1, "LOG-" + UUID.randomUUID().toString().substring(0, 16));
            ps.setString(2, action); ps.setString(3, detail); ps.executeUpdate();
        } catch (Exception ex) { System.err.println("Audit failure: " + ex.getMessage()); }
    }
    private static void login(String request, PrintWriter output) {
        String[] parts = request.split("\t", -1);
        if (parts.length != 3) { output.println("ERROR Invalid login"); return; }
        String username;
        char[] password;
        try {
            username = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            password = new String(Base64.getDecoder().decode(parts[2]), StandardCharsets.UTF_8).toCharArray();
        } catch (Exception e) { output.println("ERROR Invalid login"); return; }
        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT password_hash,role FROM users WHERE username=? AND is_active=TRUE")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString(1);
                    String[] bits = stored.split(":", -1);
                    if (bits.length == 3 && "pbkdf2".equals(bits[0])) {
                        byte[] salt = Base64.getDecoder().decode(bits[1]);
                        String candidate = hash(password, salt);
                        if (java.security.MessageDigest.isEqual(candidate.getBytes(StandardCharsets.US_ASCII),bits[2].getBytes(StandardCharsets.US_ASCII))) {
                            String role = rs.getString(2);
                            String token = UUID.randomUUID().toString();
                            SESSIONS.put(token, new Session(username, role, System.currentTimeMillis()+3600000));
                            audit("Login", username + " successful");
                            output.println("OK\t"+token+"\t"+role);
                            return;
                        }
                    }
                }
            }
            audit("Login", username + " failed");
            output.println("ERROR Invalid username or password");
        } catch (Exception ex) { output.println("ERROR Authentication unavailable"); }
        finally { java.util.Arrays.fill(password, '\0'); }
    }
    private static void seedDemoUsers() throws Exception {
        // Only seed an empty user table. Never overwrite existing accounts.
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement count = con.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet rs = count.executeQuery()) {
            rs.next(); if (rs.getInt(1) != 0) return;
            String[][] users = {{"admin","Admin"},{"coordinator","Coordinator"},{"responder","Responder"}};
            try (PreparedStatement insert = con.prepareStatement(
                    "INSERT INTO users(user_id,username,password_hash,full_name,role,is_active) VALUES(?,?,?,?,?,TRUE)")) {
                for (int i=0;i<users.length;i++) {
                    byte[] salt = new byte[16]; new java.security.SecureRandom().nextBytes(salt);
                    String initial = "DRS-" + users[i][0] + "-ChangeMe2026!";
                    String stored = "pbkdf2:" + Base64.getEncoder().encodeToString(salt) + ":" + hash(initial.toCharArray(), salt);
                    insert.setString(1,"USR-"+String.format("%03d",i+1));
                    insert.setString(2,users[i][0]);insert.setString(3,stored);
                    insert.setString(4,users[i][1]+" Demo");insert.setString(5,users[i][1]);insert.executeUpdate();
                }
            }
        }
    }


    public static void main(String[] args) {

        try { ensureAssessmentIncidentColumn(); seedDemoUsers(); } catch (Exception e) {
            System.err.println("Assessment schema migration failed: " + e.getMessage());
            return;
        }

        ExecutorService workers
                = Executors.newFixedThreadPool(10);

try (ServerSocket server = createTLSServerFactory().createServerSocket(PORT)) {
            System.out.println(
                    "DRS Server started on port " + PORT);

            while (true) {
                Socket client = server.accept();

                workers.execute(() -> handleClient(client));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workers.shutdown();
        }
        
        
    }
    
    private static SSLServerSocketFactory createTLSServerFactory() throws Exception {
        String passwordValue = null;    
    if (passwordValue == null || passwordValue.isBlank()) {
    javax.swing.JPasswordField passwordField =
            new javax.swing.JPasswordField();

    int result = javax.swing.JOptionPane.showConfirmDialog(
            null,
            passwordField,
            "Enter TLS Server Keystore Password",
            javax.swing.JOptionPane.OK_CANCEL_OPTION,
            javax.swing.JOptionPane.PLAIN_MESSAGE
    );

    if (result != javax.swing.JOptionPane.OK_OPTION) {
        throw new IllegalStateException("TLS password entry cancelled");
    }

    char[] entered = passwordField.getPassword();
    try {
        passwordValue = new String(entered);
    } finally {
        java.util.Arrays.fill(entered, '\0');
    }
}

    if (passwordValue == null || passwordValue.isBlank()) {
        throw new IllegalStateException(
            "DRS_TLS_PASSWORD environment variable is not configured"
        );
    }

    char[] password = passwordValue.toCharArray();

    try {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");

        try (FileInputStream file = new FileInputStream("tls/drs-server.p12")) {
            keyStore.load(file, password);
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm()
        );
        kmf.init(keyStore, password);

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), null, null);

        return context.getServerSocketFactory();

    } finally {
        java.util.Arrays.fill(password, '\0');
    }
}

    // Each request uses its own JDBC connection and transaction.
    private static void saveReportTransaction(String request, PrintWriter output) {
        String[] tokens = request.split("\t", -1);
        if (tokens.length != 6) {
            output.println("ERROR Invalid request format");
            return;
        }
        String[] values = new String[5];
        try {
            for (int i = 0; i < values.length; i++) {
                values[i] = new String(Base64.getDecoder().decode(tokens[i + 1]),
                        StandardCharsets.UTF_8).trim();
                if (values[i].isEmpty()) throw new IllegalArgumentException("Empty required field");
            }
            if (values[0].length() > 150 || values[1].length() > 100 ||
                    values[2].length() > 255 || values[3].length() > 30) {
                throw new IllegalArgumentException("Input exceeds database column limit");
            }
            if (!java.util.List.of("Minor", "Moderate", "Severe", "Critical").contains(values[3])) {
                throw new IllegalArgumentException("Invalid impact level");
            }
        } catch (IllegalArgumentException e) {
            output.println("ERROR Invalid report data");
            return;
        }
        String priority = switch (values[3]) {
            case "Critical" -> "Critical";
            case "Severe" -> "High";
            case "Moderate" -> "Medium";
            default -> "Low";
        };
        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 15);
        LocalDateTime now = LocalDateTime.now();
        String description = values[1] + " reported - Assistance required: "
                + values[4].replace('\n', ' ').replace('\r', ' ');
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                String incidentId;
                // Lock a dedicated counter row to avoid duplicate IDs from simultaneous clients.
                try (PreparedStatement create = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS drs_id_counters (counter_name VARCHAR(30) PRIMARY KEY, next_id INT NOT NULL)")) {
                    create.executeUpdate();
                }
                try (PreparedStatement init = connection.prepareStatement(
                        "INSERT IGNORE INTO drs_id_counters (counter_name, next_id) "
                        + "SELECT 'incident', COALESCE(MAX(CAST(SUBSTRING(incident_id, 5) AS UNSIGNED)), 0) + 1 "
                        + "FROM incidents WHERE incident_id REGEXP '^INC-[0-9]+$'")) {
                    init.executeUpdate();
                }
                int next;
                try (PreparedStatement lock = connection.prepareStatement(
                        "SELECT next_id FROM drs_id_counters WHERE counter_name='incident' FOR UPDATE");
                     ResultSet rs = lock.executeQuery()) {
                    if (!rs.next()) throw new java.sql.SQLException("Missing incident counter");
                    next = rs.getInt(1);
                }
                incidentId = String.format("INC-%03d", next);
                try (PreparedStatement advance = connection.prepareStatement(
                        "UPDATE drs_id_counters SET next_id = next_id + 1 WHERE counter_name='incident'")) {
                    advance.executeUpdate();
                }
                try (PreparedStatement insertReport = connection.prepareStatement(
                        "INSERT INTO disaster_reports (disaster_report_id, reported_by, disaster_type, "
                        + "location, impact_level, assistance_needs, submitted_at) VALUES (?,?,?,?,?,?,?)")) {
                    insertReport.setString(1, reportId);
                    for (int i = 0; i < 5; i++) insertReport.setString(i + 2, values[i]);
                    insertReport.setTimestamp(7, Timestamp.valueOf(now));
                    insertReport.executeUpdate();
                }
                try (PreparedStatement insertIncident = connection.prepareStatement(
                        "INSERT INTO incidents (incident_id, disaster_type, location, description, "
                        + "priority, status, reported_at, resolved_at, injured_count) "
                        + "VALUES (?,?,?,?,?,'Active',?,NULL,0)")) {
                    insertIncident.setString(1, incidentId);
                    insertIncident.setString(2, values[1]);
                    insertIncident.setString(3, values[2]);
                    insertIncident.setString(4, description);
                    insertIncident.setString(5, priority);
                    insertIncident.setTimestamp(6, Timestamp.valueOf(now));
                    insertIncident.executeUpdate();
                }
                connection.commit();
                output.println("OK\t" + reportId + "\t" + incidentId + "\t" + now);
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            output.println("ERROR Could not save disaster report");
        }
    }

    private static String[] decodeFields(String[] tokens) {
        String[] fields = new String[tokens.length-1];
        for (int i=1;i<tokens.length;i++) fields[i-1] = new String(Base64.getDecoder().decode(tokens[i]), StandardCharsets.UTF_8);
        return fields;
    }

    private static void saveAssessmentTransaction(String request, PrintWriter output) {
        String[] parts = request.split("\t", -1);
        if (parts.length != 8) { output.println("ERROR Invalid assessment format"); return; }
        try {
            String[] f = decodeFields(parts);
            String incidentId = f[0].trim();
            int people = Integer.parseInt(f[1]);
            String impact = f[2], access = f[3], needs = f[4], notes = f[5], priority = f[6];
            if (!incidentId.matches("INC-[0-9]+") || people < 0 ||
                !java.util.List.of("Low","Medium","High","Critical").contains(impact) ||
                !java.util.List.of("Accessible","Partially Accessible","Not Accessible").contains(access) ||
                !java.util.List.of("Low","Medium","High","Critical").contains(priority) ||
                needs.length() > 10000 || notes.length() > 10000) {
                output.println("ERROR Invalid assessment data"); return;
            }
            // Recalculate priority on the server so clients cannot forge it.
            String expected = impact;
            if ("Not Accessible".equals(access)) {
                expected = switch (impact) {
                    case "Low" -> "Medium";
                    case "Medium" -> "High";
                    case "High" -> "Critical";
                    default -> "Critical";
                };
            }
            if (!expected.equals(priority)) { output.println("ERROR Invalid calculated priority"); return; }
            String assessmentId = "ASM-" + UUID.randomUUID().toString().substring(0, 12);
            try (Connection con = DatabaseConnection.getConnection()) {
                con.setAutoCommit(false);
                try {
                    String status;
                    try (PreparedStatement check = con.prepareStatement(
                            "SELECT status FROM incidents WHERE incident_id=? FOR UPDATE")) {
                        check.setString(1, incidentId);
                        try (ResultSet rs = check.executeQuery()) {
                            if (!rs.next()) { con.rollback(); output.println("ERROR Incident not found"); return; }
                            status = rs.getString(1);
                        }
                    }
                    if ("Resolved".equals(status) || "Closed".equals(status)) {
                        con.rollback(); output.println("ERROR Incident is closed"); return;
                    }
                    try (PreparedStatement insert = con.prepareStatement(
                            "INSERT INTO assessments (assessment_id,incident_id,people_affected,impact_level,access_condition,assistance_needs,assessment_notes) VALUES (?,?,?,?,?,?,?)")) {
                        insert.setString(1,assessmentId); insert.setString(2,incidentId);
                        insert.setInt(3,people); insert.setString(4,impact);
                        insert.setString(5,access); insert.setString(6,needs); insert.setString(7,notes);
                        insert.executeUpdate();
                    }
                    try (PreparedStatement update = con.prepareStatement(
                            "UPDATE incidents SET priority=?,injured_count=?,status=CASE WHEN status='Reported' THEN 'Active' ELSE status END WHERE incident_id=?")) {
                        update.setString(1,priority); update.setInt(2,people); update.setString(3,incidentId);
                        if (update.executeUpdate() != 1) throw new java.sql.SQLException("Incident update failed");
                    }
                    con.commit(); output.println("OK\t" + assessmentId);
                } catch (Exception e) { con.rollback(); throw e; }
            }
        } catch (Exception e) {
            e.printStackTrace(); output.println("ERROR Could not save assessment");
        }
    }

    // Migration for databases created before assessments were linked to incidents.
    private static void ensureAssessmentIncidentColumn() throws Exception {
        try (Connection con = DatabaseConnection.getConnection()) {
            boolean exists;
            try (ResultSet rs = con.getMetaData().getColumns(con.getCatalog(), null, "assessments", "incident_id")) {
                exists = rs.next();
            }
            if (!exists) {
                try (PreparedStatement ps = con.prepareStatement(
                        "ALTER TABLE assessments ADD COLUMN incident_id VARCHAR(20) NULL")) {
                    ps.executeUpdate();
                }
            }
        }
    }

    private static void handleClient(Socket socket) {

        try (Socket client = socket; BufferedReader input = new BufferedReader(
                new InputStreamReader(
                        client.getInputStream())); PrintWriter output = new PrintWriter(
                client.getOutputStream(), true)) {

            String request;

            while ((request = input.readLine()) != null) {

                if (request.startsWith("LOGIN_V1\t")) { login(request, output); continue; }
                if (request.startsWith("LOGOUT_V1\t")) {
                    String token = request.substring("LOGOUT_V1\t".length());
                    Session old = SESSIONS.remove(token);
                    if (old != null) audit("Logout", old.username());
                    output.println("OK"); continue;
                }
                if ("PING".equals(request)) {
                    output.println("PONG"); continue;

                } else if (request.startsWith("AUTH_V1\t")) {
                    String[] envelope = request.split("\t", 3);
                    if (envelope.length != 3) { output.println("ERROR Authentication required"); continue; }
                    Session session = SESSIONS.get(envelope[1]);
                    if (session == null || session.expires() < System.currentTimeMillis()) {
                        output.println("ERROR Session expired or invalid"); continue;
                    }
                    request = envelope[2];
                    String role = session.role();
                    boolean read = request.equals("GET_INCIDENTS_V2") || request.equals("GET_INCIDENTS")
                            || request.equals("COUNT_INCIDENTS") || request.equals("GET_TASKS_V1")
                            || request.equals("GET_DEPARTMENTS_V1");
                    boolean responderWrite = request.startsWith("SUBMIT_REPORT\t") || request.startsWith("UPDATE_TASK_STATUS_V1\t");
                    boolean privilegedWrite = request.startsWith("SUBMIT_ASSESSMENT_V1\t")
                            || request.startsWith("UPDATE_DEPARTMENT_V1\t") || request.startsWith("ADD_TASK_V1\t")
                            || request.startsWith("AUTO_ASSIGN_TASK_V1\t") || request.startsWith("ESCALATE_INCIDENT_V1\t");
                    if (!(read || (responderWrite && java.util.List.of("Admin","Coordinator","Responder").contains(role))
                            || (privilegedWrite && java.util.List.of("Admin","Coordinator").contains(role)))) {
                        output.println("ERROR Permission denied"); audit("Other", session.username()+" denied: "+request.split("\t")[0]); continue;
                    }
                    if (!read) audit("Other", session.username()+" requested: "+request.split("\t")[0]);
                } else { output.println("ERROR Authentication required"); continue; }
                if (request.startsWith("SUBMIT_ASSESSMENT_V1\t")) {
                    saveAssessmentTransaction(request, output);

                } else if (request.startsWith("SUBMIT_REPORT\t")) {
                    saveReportTransaction(request, output);

                } else if ("GET_INCIDENTS_V2".equals(request)) {
                    // One Base64-encoded, tab-separated record per line.
                    // A count header prevents confusion with user-supplied text.
                    String sql = "SELECT incident_id, disaster_type, location, "
                            + "description, priority, status, reported_at, "
                            + "injured_count FROM incidents ORDER BY incident_id";
                    try (Connection connection = DatabaseConnection.getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet rs = statement.executeQuery()) {
                        java.util.List<String> rows = new java.util.ArrayList<>();
                        while (rs.next()) {
                            String[] fields = {
                                rs.getString("incident_id"),
                                rs.getString("disaster_type"),
                                rs.getString("location"),
                                rs.getString("description"),
                                rs.getString("priority"),
                                rs.getString("status"),
                                rs.getString("reported_at"),
                                Integer.toString(rs.getInt("injured_count"))
                            };
                            StringBuilder row = new StringBuilder();
                            for (int i = 0; i < fields.length; i++) {
                                if (i > 0) row.append('\t');
                                row.append(Base64.getEncoder().encodeToString(
                                    (fields[i] == null ? "" : fields[i])
                                        .getBytes(StandardCharsets.UTF_8)));
                            }
                            rows.add(row.toString());
                        }
                        output.println("OK " + rows.size());
                        for (String row : rows) output.println(row);
                    } catch (Exception e) {
                        output.println("ERROR Database query failed");
                        e.printStackTrace();
                    }

                } else if ("GET_INCIDENTS".equals(request)) {

                    String sql = """
        SELECT incident_id, disaster_type, location,
               description, priority, status,
               reported_at, injured_count
        FROM incidents
        ORDER BY incident_id
        """;

                    try (Connection connection
                            = DatabaseConnection.getConnection(); PreparedStatement statement
                            = connection.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

                        while (rs.next()) {
                            output.println(
                                    rs.getString("incident_id") + "|"
                                    + rs.getString("disaster_type") + "|"
                                    + rs.getString("location") + "|"
                                    + rs.getString("description") + "|"
                                    + rs.getString("priority") + "|"
                                    + rs.getString("status") + "|"
                                    + rs.getString("reported_at") + "|"
                                    + rs.getInt("injured_count")
                            );
                        }

                        output.println("END");

                    } catch (Exception e) {
                        output.println("ERROR: Failed to retrieve incidents");
                        e.printStackTrace();
                    }

                } else if ("GET_DEPARTMENTS_V1".equals(request)) {
                    String sql = "SELECT department_id, name, type, contact_details, availability "
                            + "FROM departments ORDER BY department_id";
                    try (Connection connection = DatabaseConnection.getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet rs = statement.executeQuery()) {
                        java.util.List<String> rows = new java.util.ArrayList<>();
                        while (rs.next()) {
                            String[] fields = {
                                rs.getString("department_id"), rs.getString("name"),
                                rs.getString("type"), rs.getString("contact_details"),
                                rs.getString("availability")
                            };
                            java.util.List<String> encoded = new java.util.ArrayList<>();
                            for (String field : fields) {
                                encoded.add(Base64.getEncoder().encodeToString(
                                    (field == null ? "" : field).getBytes(StandardCharsets.UTF_8)));
                            }
                            rows.add(String.join("\t", encoded));
                        }
                        output.println("OK " + rows.size());
                        for (String row : rows) output.println(row);
                    } catch (Exception e) {
                        e.printStackTrace();
                        output.println("ERROR Database query failed");
                    }

                } else if (request.startsWith("AUTO_ASSIGN_TASK_V1\t")) {
                    // Atomic assignment: one available department is reserved for one pending task.
                    String[] parts = request.split("\t", -1);
                    if (parts.length != 2) { output.println("ERROR Invalid assignment request"); continue; }
                    try {
                        String taskId = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                        if (!taskId.matches("[A-Za-z0-9_-]{1,30}")) { output.println("ERROR Invalid task ID"); continue; }
                        try (Connection con = DatabaseConnection.getConnection()) {
                            con.setAutoCommit(false);
                            try {
                                String description, assigned, status, incidentType = "";
                                try (PreparedStatement ps = con.prepareStatement(
                                        "SELECT description, assigned_department, status, incident_id FROM response_tasks WHERE task_id=? FOR UPDATE")) {
                                    ps.setString(1, taskId);
                                    try (ResultSet rs = ps.executeQuery()) {
                                        if (!rs.next()) { output.println("ERROR Task not found"); con.rollback(); continue; }
                                        description = rs.getString(1); assigned = rs.getString(2); status = rs.getString(3);
                                        String incidentId = rs.getString(4);
                                        if (incidentId != null && !incidentId.isBlank()) {
                                            try (PreparedStatement ip = con.prepareStatement("SELECT disaster_type FROM incidents WHERE incident_id=?")) {
                                                ip.setString(1, incidentId);
                                                try (ResultSet ir = ip.executeQuery()) { if (ir.next()) incidentType = ir.getString(1); }
                                            }
                                        }
                                    }
                                }
                                if (!"Pending".equals(status)) { output.println("ERROR Only pending tasks can be assigned"); con.rollback(); continue; }
                                // Existing non-placeholder assignments must not be silently overwritten.
                                if (assigned != null && !assigned.isBlank() && !assigned.equalsIgnoreCase("Unassigned")
                                        && !assigned.equalsIgnoreCase("Not Assigned") && !assigned.equalsIgnoreCase("None")) {
                                    output.println("ERROR Task already assigned to " + assigned); con.rollback(); continue;
                                }
                                String keywords = (description + " " + incidentType).toLowerCase(java.util.Locale.ROOT);
                                String preferred = com.drs.drs.initial.features.DepartmentMatcher.preferredType(keywords);
                                String departmentId = null, departmentName = null;
                                // Lock eligible departments in stable ID order to avoid double assignment.
                                try (PreparedStatement ps = con.prepareStatement(
                                        "SELECT department_id,name,type FROM departments WHERE availability='Available' " +
                                        "ORDER BY CASE WHEN LOWER(type) LIKE ? OR LOWER(name) LIKE ? THEN 0 ELSE 1 END, department_id LIMIT 1 FOR UPDATE")) {
                                    String match = "%" + preferred.toLowerCase(java.util.Locale.ROOT) + "%";
                                    ps.setString(1, match); ps.setString(2, match);
                                    try (ResultSet rs = ps.executeQuery()) {
                                        if (rs.next()) { departmentId = rs.getString(1); departmentName = rs.getString(2); }
                                    }
                                }
                                if (departmentId == null) { output.println("ERROR No available department"); con.rollback(); continue; }
                                try (PreparedStatement ps = con.prepareStatement(
                                        "UPDATE response_tasks SET assigned_department=? WHERE task_id=? AND status='Pending'")) {
                                    ps.setString(1, departmentName); ps.setString(2, taskId);
                                    if (ps.executeUpdate() != 1) throw new java.sql.SQLException("Task changed");
                                }
                                try (PreparedStatement ps = con.prepareStatement(
                                        "UPDATE departments SET availability='Busy' WHERE department_id=? AND availability='Available'")) {
                                    ps.setString(1, departmentId);
                                    if (ps.executeUpdate() != 1) throw new java.sql.SQLException("Department no longer available");
                                }
                                con.commit(); audit("Task Assigned", taskId + " -> " + departmentName);
                                output.println("OK " + Base64.getEncoder().encodeToString(departmentName.getBytes(StandardCharsets.UTF_8)));
                            } catch (Exception ex) { con.rollback(); throw ex; }
                        }
                    } catch (Exception ex) { ex.printStackTrace(); output.println("ERROR Assignment failed"); }

                } else if (request.startsWith("ESCALATE_INCIDENT_V1\t")) {
                    String[] parts = request.split("\t", -1);
                    if (parts.length != 2) { output.println("ERROR Invalid escalation request"); continue; }
                    try {
                        String incidentId = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);
                        if (!incidentId.matches("[A-Za-z0-9_-]{1,30}")) { output.println("ERROR Invalid incident ID"); continue; }
                        try (Connection con = DatabaseConnection.getConnection()) {
                            con.setAutoCommit(false);
                            try {
                                String oldPriority, status;
                                try (PreparedStatement ps = con.prepareStatement(
                                        "SELECT priority,status FROM incidents WHERE incident_id=? FOR UPDATE")) {
                                    ps.setString(1, incidentId);
                                    try (ResultSet rs = ps.executeQuery()) {
                                        if (!rs.next()) { output.println("ERROR Incident not found"); con.rollback(); continue; }
                                        oldPriority = rs.getString(1); status = rs.getString(2);
                                    }
                                }
                                if ("Resolved".equalsIgnoreCase(status) || "Closed".equalsIgnoreCase(status)) {
                                    output.println("ERROR Cannot escalate resolved or closed incidents"); con.rollback(); continue;
                                }
                                String next = com.drs.drs.initial.features.SeverityEscalation.nextPriority(oldPriority);
                                if (next.equals(oldPriority)) { output.println("ERROR Already at Critical priority"); con.rollback(); continue; }
                                try (PreparedStatement ps = con.prepareStatement(
                                        "UPDATE incidents SET priority=?, status=CASE WHEN status='Reported' THEN 'Active' ELSE status END WHERE incident_id=?")) {
                                    ps.setString(1, next); ps.setString(2, incidentId); ps.executeUpdate();
                                }
                                con.commit(); audit("Status Changed", "Incident " + incidentId + " escalated " + oldPriority + " -> " + next);
                                output.println("OK " + next);
                            } catch (Exception ex) { con.rollback(); throw ex; }
                        }
                    } catch (Exception ex) { ex.printStackTrace(); output.println("ERROR Escalation failed"); }

                } else if (request.startsWith("UPDATE_DEPARTMENT_V1\t")) {
                    String[] parts = request.split("\t", -1);
                    if (parts.length != 3) {
                        output.println("ERROR Invalid request");
                        continue;
                    }
                    try {
                        String departmentId = new String(Base64.getDecoder().decode(parts[1]),
                                StandardCharsets.UTF_8);
                        String availability = new String(Base64.getDecoder().decode(parts[2]),
                                StandardCharsets.UTF_8);
                        if (departmentId.isBlank() || departmentId.length() > 20 ||
                                !java.util.List.of("Available", "Busy", "Unavailable")
                                        .contains(availability)) {
                            output.println("ERROR Invalid department data");
                            continue;
                        }
                        try (Connection connection = DatabaseConnection.getConnection();
                             PreparedStatement statement = connection.prepareStatement(
                                "UPDATE departments SET availability=? WHERE department_id=?")) {
                            statement.setString(1, availability);
                            statement.setString(2, departmentId);
                            output.println(statement.executeUpdate() == 1
                                    ? "OK" : "ERROR Department not found");
                        }
                    } catch (IllegalArgumentException e) {
                        output.println("ERROR Invalid encoding");
                    } catch (Exception e) {
                        e.printStackTrace();
                        output.println("ERROR Database update failed");
                    }


                } else if ("GET_TASKS_V1".equals(request)) {
                    try (Connection con = DatabaseConnection.getConnection();
                         PreparedStatement ps = con.prepareStatement(
                            "SELECT task_id, incident_id, description, status, assigned_department, created_at, target_time, completed_at, notes FROM response_tasks ORDER BY task_id");
                         ResultSet rs = ps.executeQuery()) {
                        java.util.List<String> rows = new java.util.ArrayList<>();
                        while (rs.next()) {
                            String[] fields = {rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)};
                            java.util.List<String> encoded = new java.util.ArrayList<>();
                            for (String f : fields) encoded.add(Base64.getEncoder().encodeToString((f == null ? "" : f).getBytes(StandardCharsets.UTF_8)));
                            rows.add(String.join("\t", encoded));
                        }
                        output.println("OK " + rows.size());
                        for (String row : rows) output.println(row);
                    } catch (Exception e) { e.printStackTrace(); output.println("ERROR Could not load tasks"); }

                } else if (request.startsWith("ADD_TASK_V1\t")) {
                    String[] p = request.split("\t", -1);
                    if (p.length != 10) { output.println("ERROR Invalid task format"); continue; }
                    try {
                        String[] f = decodeFields(p);
                        if (f[0].isBlank() || f[0].length() > 30 || f[2].isBlank() ||
                                !"Pending".equals(f[3]) || f[4].isBlank()) throw new IllegalArgumentException("Invalid task");
                        try (Connection con = DatabaseConnection.getConnection();
                             PreparedStatement ps = con.prepareStatement("INSERT INTO response_tasks (task_id,incident_id,description,status,assigned_department,created_at,target_time,completed_at,notes) VALUES (?,?,?,?,?,?,?,?,?)")) {
                            for (int i=0;i<9;i++) {
                                if (i == 5 || i == 6 || i == 7) ps.setTimestamp(i+1, f[i].isBlank() ? null : Timestamp.valueOf(f[i]));
                                else ps.setString(i+1, f[i].isBlank() && i==1 ? null : f[i]);
                            }
                            ps.executeUpdate();
                            output.println("OK");
                        }
                    } catch (Exception e) { e.printStackTrace(); output.println("ERROR Could not create task"); }

                } else if (request.startsWith("UPDATE_TASK_STATUS_V1\t")) {
                    String[] p = request.split("\t", -1);
                    if (p.length != 3) { output.println("ERROR Invalid status request"); continue; }
                    try {
                        String id = new String(Base64.getDecoder().decode(p[1]), StandardCharsets.UTF_8);
                        String next = new String(Base64.getDecoder().decode(p[2]), StandardCharsets.UTF_8);
                        if (id.isBlank() || id.length() > 30 || !java.util.List.of("In Progress","On Hold","Completed","Cancelled").contains(next)) throw new IllegalArgumentException();
                        String sql = "UPDATE response_tasks SET status=?, completed_at=CASE WHEN ?='Completed' THEN CURRENT_TIMESTAMP ELSE completed_at END WHERE task_id=? AND (" +
                                "(status='Pending' AND ? IN ('In Progress','Cancelled')) OR " +
                                "(status='In Progress' AND ? IN ('On Hold','Completed','Cancelled')) OR " +
                                "(status='On Hold' AND ? IN ('In Progress','Completed','Cancelled')))";
                        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                            ps.setString(1,next);ps.setString(2,next);ps.setString(3,id);
                            ps.setString(4,next);ps.setString(5,next);ps.setString(6,next);
                            output.println(ps.executeUpdate()==1 ? "OK" : "ERROR Invalid transition or missing task");
                        }
                    } catch (Exception e) { e.printStackTrace(); output.println("ERROR Could not update task"); }

                } else if ("COUNT_INCIDENTS".equals(request)) {

                    String sql = "SELECT COUNT(*) FROM incidents";

                    try (Connection connection
                            = DatabaseConnection.getConnection(); PreparedStatement statement
                            = connection.prepareStatement(sql); ResultSet resultSet
                            = statement.executeQuery()) {

                        if (resultSet.next()) {
                            output.println(resultSet.getInt(1));
                        } else {
                            output.println("0");
                        }

                    } catch (Exception e) {
                        output.println("ERROR: Database query failed");
                        e.printStackTrace();
                    }

                } else {
                    output.println("ERROR: Unknown request");
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "Client connection ended: " + e.getMessage());
        }
    }
}
