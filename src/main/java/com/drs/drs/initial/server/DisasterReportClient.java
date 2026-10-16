package com.drs.drs.initial.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class DisasterReportClient {
    private DisasterReportClient() {}

    public static String[] submit(String reportedBy, String type, String location,
                                  String impact, String needs) throws Exception {
        String[] fields = {reportedBy, type, location, impact, needs};
        StringBuilder request = new StringBuilder("SUBMIT_REPORT");
        for (String field : fields) {
            request.append('\t').append(Base64.getEncoder().encodeToString(
                    field.getBytes(StandardCharsets.UTF_8)));
        }
        try (Socket socket = com.drs.drs.initial.server.TLSConnection.open()) {
            socket.setSoTimeout(10000);
            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                 BufferedReader in = new BufferedReader(
                         new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                out.println(com.drs.drs.initial.server.DRSSession.wrap(request.toString()));
                String response = in.readLine();
                if (response == null || !response.startsWith("OK\t")) {
                    throw new java.io.IOException(response == null ? "Server disconnected" : response);
                }
                String[] parts = response.split("\t", -1);
                if (parts.length != 4) throw new java.io.IOException("Invalid server response");
                return parts; // OK, report ID, incident ID, submission timestamp
            }
        }
    }
}
