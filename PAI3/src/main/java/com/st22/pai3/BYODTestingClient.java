package com.st22.pai3;

import javax.net.ssl.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.time.Duration;
import java.time.Instant;

public class BYODTestingClient {

    private static final String[] protocols = new String[] {"TLSv1.3"};
    private static final String[] chiperSuits = new String[] {"TLS_AES_128_GCM_SHA256"};

    public static void main(String[] args) {
        Instant start = Instant.now();
        for (int i = 1; i <= 300; i++) {
            String usuario = "user" + i;
            String pass = "myPassword" + i;
            String msg = "Prueba del user " + i;

            new BYODTestingClient(usuario,pass,msg);
        }
        Instant end = Instant.now();
        Duration interval = Duration.between(start, end);
        System.err.println("Execution time in seconds: " + 	interval.getSeconds());
    }

    public BYODTestingClient(String username, String password, String message) {
        SSLSocket client;
        String address = "localhost";
        int port = 6300;

        try {
            System.out.println("client start");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("src/main/certs/client/clientKey.jks"),
                    "Casillas1227".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "Casillas1227".toCharArray());

            KeyStore trustedStore = KeyStore.getInstance("JKS");
            trustedStore.load(new FileInputStream(
                    "src/main/certs/client/clientTrustedCerts.jks"), "Casillas1227"
                    .toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustedStore);

            SSLContext sc = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            KeyManager[] keyManagers = kmf.getKeyManagers();
            sc.init(keyManagers, trustManagers, null);

            SSLSocketFactory ssf = sc.getSocketFactory();
            client = (SSLSocket) ssf.createSocket(address, port);
            client.setEnabledProtocols(protocols);
            client.setEnabledCipherSuites(chiperSuits);
            client.startHandshake();

            PrintWriter output = new PrintWriter(client.getOutputStream());
            output.println(username);
            output.println(password);
            output.println(message);

            output.flush();
            System.out.println("Mensaje enviado");
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            String received = input.readLine();
            System.out.println("Mensaje recibido");
            client.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
