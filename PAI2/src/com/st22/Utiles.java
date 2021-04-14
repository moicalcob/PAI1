package com.st22;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utiles {
    
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    
    public static Boolean nonceIsValid(String nonce) throws URISyntaxException, IOException {
        boolean nonceValid;
        Path path = Paths.get("./nonces_server.txt");

        Stream<String> lines = Files.lines(path);
        List<String> allNonces = lines.collect(Collectors.toList());
        nonceValid = allNonces.stream().noneMatch((line) -> line.equals(nonce));
        lines.close();
        return nonceValid;
    }

    public static void storeNonce(String nonce) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("nonces_server.txt", true));
        writer.append("\n").append(nonce);
        writer.close();
    }

    public static void messageVerificationFailed(String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("logs_server.txt", true));
        writer.append("\n").append("[ERROR] Error al comprobar la integridad del mensaje: ").append(message)
                .append("   ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")));
        writer.close();
    }

    public static void saveTransaction(String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("transactions_server.txt", true));
        writer.append("\n").append("[DONE] Transacción realizada: ").append(message)
                .append("   ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")));
        writer.close();
    }

    public static double getKpi() throws IOException {
        if(getNumTransactionsOk() > 0) {
            return (double) getNumTransactionsOk() / (getNumTransactionsWrong() + getNumTransactionsOk());
        }
        return 0;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static Integer getNumTransactionsOk() throws IOException {
        LineNumberReader reader  = new LineNumberReader(new FileReader("./transactions_server.txt"));
        int cnt = 0;
        String lineRead = "";
        while ((lineRead = reader.readLine()) != null) {}

        cnt = reader.getLineNumber();
        reader.close();
        return cnt - 1;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static Integer getNumTransactionsWrong() throws IOException {
        LineNumberReader reader  = new LineNumberReader(new FileReader("./logs_server.txt"));
        int cnt = 0;
        String lineRead = "";
        while ((lineRead = reader.readLine()) != null) {}

        cnt = reader.getLineNumber();
        reader.close();
        return cnt - 1;
    }
}
