package com.st22.pai3;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;

public class BYODServer {

    private static final String[] protocols = new String[] {"TLSv1.3"};
    private static final String[] chiperSuits = new String[] {"TLS_AES_128_GCM_SHA256"};

    public static void main(String[] args) {
        SSLServerSocket serverSocket;
        int port = 6300;

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("src/main/certs/server/serverKey.jks"),
                    "Casillas1227".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "Casillas1227".toCharArray());

            KeyStore trustedStore = KeyStore.getInstance("JKS");
            trustedStore.load(new FileInputStream(
                    "src/main/certs/server/serverTrustedCerts.jks"), "Casillas1227"
                    .toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustedStore);

            SSLContext sc = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            KeyManager[] keyManagers = kmf.getKeyManagers();
            sc.init(keyManagers, trustManagers, null);

            SSLServerSocketFactory ssf = sc.getServerSocketFactory();
            serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
            serverSocket.setEnabledProtocols(protocols);
            serverSocket.setEnabledCipherSuites(chiperSuits);

            while (true) {
                System.out.println("Servidor esperando mensajes");
                Socket aClient = serverSocket.accept();
                System.out.println("client accepted");
                aClient.setSoLinger(true, 1000);
                BufferedReader input = new BufferedReader(new InputStreamReader(
                        aClient.getInputStream()));
                String username = input.readLine();
                String password = input.readLine();
                String messageClient = input.readLine();

                Boolean userValid = false;
                FileReader linesToRead = new FileReader("users.txt");

                try (BufferedReader br = new BufferedReader(linesToRead)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if(line.equals(username.concat("||").concat(password))) {
                            userValid = true;
                        }
                    }
                }
                PrintWriter output = new PrintWriter(aClient.getOutputStream());

                if(!userValid) {
                    output.println("Lo siento, según la solución del Security Team 22 su nombre de usuario o contraseña no son correctos, intentelo de nuevo");
                } else {
                    File archivo = new File("messages.txt");
                    BufferedWriter bWriter = new BufferedWriter(new FileWriter(archivo, true));
                    bWriter.write(messageClient + "\n");
                    bWriter.close();
                    output.println("Su mensaje ha sido almacenado correctamente. Muchas gracias por parte del Security Team 22");
                }
                System.out.println("Recibido " + messageClient);
                output.flush();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
