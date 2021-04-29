package com.st22.pai3;

import javax.net.ssl.*;
import javax.swing.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

public class BYODClient {

    private static final String[] protocols = new String[] {"TLSv1.3"};
    private static final String[] chiperSuits = new String[] {"TLS_AES_128_GCM_SHA256"};

    public static void main(String[] args) {
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
            String username = JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario");
            output.println(username);

            String password = JOptionPane.showInputDialog(null, "Introduce tu contrase�a");
            output.println(password);

            String clientMessage = JOptionPane.showInputDialog(null, "Introduce tu mensaje");
            output.println(clientMessage);

            output.flush();
            System.out.println("Mensaje enviado");
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            String received = input.readLine();
            JOptionPane.showMessageDialog(null, received);
            client.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
