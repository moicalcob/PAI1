package com.st22;

import javax.net.SocketFactory;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Client {

    public static void main(String[] args) {
        Client client = new Client();
        client.sendMessage();
    }

    public Client() {
    }

    private void sendMessage() {
        try {
            SocketFactory socketFactory = SocketFactory.getDefault();
            Socket client = socketFactory.createSocket("localhost", 5200);
            PrintWriter clientOutput = new PrintWriter(client.getOutputStream(), true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Generamos el nonce
            byte[] nonceBytes = new byte[32];
            SecureRandom rand = SecureRandom.getInstance("DRBG");
            rand.nextBytes(nonceBytes);
            String nonce = NonceGenerator.convertBytesToHex(nonceBytes);

            String message = JOptionPane.showInputDialog(
                    "Introduce los datos",
                    JOptionPane.QUESTION_MESSAGE);

            clientOutput.println(message);
            clientOutput.println(nonce);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
