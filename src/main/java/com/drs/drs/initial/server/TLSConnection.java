package com.drs.drs.initial.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Creates certificate-validated TLS connections to the DRS server.
 */
public final class TLSConnection {

    private TLSConnection() {
    }

    public static Socket open() throws IOException {
        return open("localhost", 5050, 3000);
    }

    public static Socket open(String host, int port, int timeoutMs) throws IOException {
        System.out.println("TLS Truststore: "
                + System.getProperty("javax.net.ssl.trustStore"));

        System.out.println("TLS Truststore Type: "
                + System.getProperty("javax.net.ssl.trustStoreType"));

        String password = System.getProperty(
                "javax.net.ssl.trustStorePassword");

        System.out.println("TLS Password Configured: "
                + (password != null
                && !password.isBlank()
                && !password.contains("${")));
        SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket();
        SSLParameters parameters = socket.getSSLParameters();
        parameters.setEndpointIdentificationAlgorithm("HTTPS");
        socket.setSSLParameters(parameters);
        try {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.setSoTimeout(5000);
            socket.startHandshake();
            return socket;
        } catch (IOException | RuntimeException e) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            throw e;
        }
    }
}
