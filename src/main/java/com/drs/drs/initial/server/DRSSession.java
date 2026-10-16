package com.drs.drs.initial.server;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
public final class DRSSession {
    private static volatile String token;
    private static volatile String role;
    private DRSSession() {}
    public static synchronized String login(String username, String password) throws Exception {
        try (java.net.Socket socket = com.drs.drs.initial.server.TLSConnection.open();
             java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(),true,StandardCharsets.UTF_8);
             java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8))) {
            socket.setSoTimeout(5000);
            out.println("LOGIN_V1\t"+encode(username)+"\t"+encode(password));
            String response = in.readLine();
            if (response == null || !response.startsWith("OK\t")) throw new java.io.IOException(response == null ? "Server unavailable" : response);
            String[] parts = response.split("\t",-1);
            if (parts.length != 3) throw new java.io.IOException("Invalid login response");
            token = parts[1]; role = parts[2]; return role;
        }
    }
    private static String encode(String value) { return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }
    public static String wrap(String command) {
        String t = token;
        if (t == null) throw new IllegalStateException("Please log in first");
        return "AUTH_V1\t"+t+"\t"+command;
    }
    public static String role() { return role; }
    public static synchronized void logout() {
        String t=token; token=null; role=null;
        if(t==null)return;
        try(java.net.Socket socket=com.drs.drs.initial.server.TLSConnection.open();
            java.io.PrintWriter out=new java.io.PrintWriter(socket.getOutputStream(),true,StandardCharsets.UTF_8)) {
            out.println("LOGOUT_V1\t"+t);
        } catch(Exception ignored) {}
    }
}
