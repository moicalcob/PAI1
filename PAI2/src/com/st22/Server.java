package com.st22;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {
    public static void main(String[] args) throws IOException {
        try {
            Server server = new Server();
            server.startServer();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private final ServerSocket server;

    public Server() throws IOException {
        ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();
        server = socketFactory.createServerSocket(5200);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startServer() throws IOException, URISyntaxException {
        do {
            System.out.println("Servidor configurado correctamente");
            Socket socket = server.accept();
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Recibimos el mensaje
            String message = bufferedReader.readLine();

            // Recibimos el nonce
            String nonce = bufferedReader.readLine();

            //Checkeamos el nonce
            Boolean nonceValid = nonceIsValid(nonce);
            if (nonceValid) {
                storeNonce(nonce);
                System.out.println("Nonce válido");
            } else {
                messageVerificationFailed(message);
                System.out.println("Nonce no válido");
            }

        } while (true);
    }

    private Boolean nonceIsValid(String nonce) throws URISyntaxException, IOException {
        boolean nonceValid;
        Path path = Paths.get("./nonces.txt");

        Stream<String> lines = Files.lines(path);
        List<String> allNonces = lines.collect(Collectors.toList());
        nonceValid = allNonces.stream().noneMatch((line) -> line.equals(nonce));
        lines.close();
        return nonceValid;
    }

    private void storeNonce(String nonce) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("nonces.txt", true));
        writer.append("\n").append(nonce);
        writer.close();
    }

    private void messageVerificationFailed(String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true));
        writer.append("\n").append("[ERROR] Error al comprobar la integridad del mensaje: ").append(message)
                .append("   ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")));
        writer.close();
    }
}
