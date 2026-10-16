package com.drs.drs.initial.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DRSClientTest {

    public static void main(String[] args) {

        try (Socket socket
                = com.drs.drs.initial.server.TLSConnection.open(); PrintWriter output = new PrintWriter(
                        socket.getOutputStream(), true); BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()))) {

            output.println("GET_INCIDENTS");

            String response;

            while ((response = input.readLine()) != null) {

                if ("END".equals(response)) {
                    break;
                }

                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
