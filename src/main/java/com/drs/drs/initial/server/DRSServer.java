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

public class DRSServer {

    private static final int PORT = 5050;

    public static void main(String[] args) {

        ExecutorService workers
                = Executors.newFixedThreadPool(10);

        try (ServerSocket server = new ServerSocket(PORT)) {

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

    private static void handleClient(Socket socket) {

        try (Socket client = socket; BufferedReader input = new BufferedReader(
                new InputStreamReader(
                        client.getInputStream())); PrintWriter output = new PrintWriter(
                client.getOutputStream(), true)) {

            String request;

            while ((request = input.readLine()) != null) {

                if ("PING".equals(request)) {
                    output.println("PONG");

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
