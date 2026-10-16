package com.drs.drs.initial.server;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
/** Authenticated commands for new disaster-response workflows. */
public final class DRSOperationsClient {
    private DRSOperationsClient() {}
    private static String send(String operation, String id) {
        try (Socket socket = com.drs.drs.initial.server.TLSConnection.open()) {
            socket.setSoTimeout(7000);
            try (PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                out.println(DRSSession.wrap(operation + "\t" + Base64.getEncoder().encodeToString(id.getBytes(StandardCharsets.UTF_8))));
                String reply = in.readLine();
                if (reply == null || !reply.startsWith("OK ")) throw new IOException(reply == null ? "Server disconnected" : reply);
                return reply.substring(3);
            }
        } catch (IOException ex) { throw new IllegalStateException("Server operation failed: " + ex.getMessage(), ex); }
    }
    public static String assignDepartment(String taskId) {
        return new String(Base64.getDecoder().decode(send("AUTO_ASSIGN_TASK_V1", taskId)), StandardCharsets.UTF_8);
    }
    public static String escalateIncident(String incidentId) {
        return send("ESCALATE_INCIDENT_V1", incidentId);
    }
}
