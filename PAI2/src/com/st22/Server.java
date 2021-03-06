package com.st22;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ServerSocketFactory;
import javax.swing.text.Utilities;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class Server {

    protected static final String SecretKey = "Secreto-ST-22";
    protected Mac mac_cliente_SHA256;

    public static void main(String[] args) throws IOException {
        try {
            Server server = new Server();
            server.startServer();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private final ServerSocket server;

    public Server() throws IOException {
        ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();
        server = socketFactory.createServerSocket(5200);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void startServer() throws IOException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        do {
            System.err.println("Servidor configurado correctamente");
            double kpi = Utiles.getKpi();
            System.out.println(kpi * 100 + "% de transacciones íntegras");
            Socket socket = server.accept();
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            PrintWriter serverOutput = new PrintWriter(outputStream, true);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Recibimos el mensaje
            String message = bufferedReader.readLine();

            // Recibimos el nonce
            String nonce = bufferedReader.readLine();

            String macMensajeDelCliente = bufferedReader.readLine();

            //Ahora se debe calcular la MAC por parte del Servidor y ver si es un mensaje integro o no
            this.mac_cliente_SHA256 = Mac.getInstance("HmacSHA256");

            SecretKeySpec key = new SecretKeySpec(SecretKey.getBytes(), "HmacSHA256");
            this.mac_cliente_SHA256.init(key);

            String nonceMessage = message.concat(nonce);
            byte[] nonceMessageByte = nonceMessage.getBytes(StandardCharsets.UTF_8);
            byte[] digest = this.mac_cliente_SHA256.doFinal(nonceMessageByte);

            String macMensajeCalculadoServ = Utiles.bytesToHex(digest);

            //Checkeamos el nonce
            boolean nonceValid = Utiles.nonceIsValid(nonce);
            if (nonceValid) {
                Utiles.storeNonce(nonce);
                System.out.println("Nonce válido");
                System.out.println("Cifrado hex: " + macMensajeDelCliente);
            } else {
                Utiles.messageVerificationFailed(message + " NONCE_NON_VALID ");
                System.out.println("Nonce no válido");
            }

            if (macMensajeDelCliente.equals(macMensajeCalculadoServ) && nonceValid) {
                System.out.println("Mensaje enviado integramente");

                Utiles.saveTransaction(message);
                serverOutput.println(message);

                //Construccion del Nonce de respuesta del servidor
                byte[] nonceBytes = new byte[32];
                SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
                rand.nextBytes(nonceBytes);
                String noncePass = NonceGenerator.convertBytesToHex(nonceBytes);
                serverOutput.println(noncePass);

                // Generar la Key apartir de la clave secreta entre servidor y cliente
                SecretKeySpec keyPass = new SecretKeySpec(SecretKey.getBytes(), "HmacSHA256");

                // Cosntruimos la nueva mac
                this.mac_cliente_SHA256 = Mac.getInstance("HmacSHA256");
                this.mac_cliente_SHA256.init(keyPass);

                String mensajeNoncePass = "Mensaje enviado integro " + noncePass;

                byte[] bPass = mensajeNoncePass.getBytes(StandardCharsets.UTF_8);
                byte[] digestPass = this.mac_cliente_SHA256.doFinal(bPass);
                String digestHexPass = Utiles.bytesToHex(digestPass);

                // Habría que calcular el correspondiente MAC con la clave compartida por servidor/cliente
                serverOutput.println(digestHexPass);
                // Importante para que el mensaje se envie
                serverOutput.flush();
            } else if (nonceValid) {
                System.err.println("Mensaje enviado no esta integro");

                Utiles.messageVerificationFailed(message + " MAC_VERIFICATION_FAILED ");

                //Construccion del Nonce de respuesta del servidor
                byte[] nonceBytes = new byte[32];
                SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
                rand.nextBytes(nonceBytes);
                String nonce_pass = NonceGenerator.convertBytesToHex(nonceBytes);
                serverOutput.println(nonce_pass);

                // Generar la Key apartir de la clave secreta entre servidor y cliente
                SecretKeySpec key_pass = new SecretKeySpec(SecretKey.getBytes(), "HmacSHA256");

                //Cosntuimos la mac con el mensaje no integro interceptado
                final Mac mac_SHA256_pass = Mac.getInstance("HmacSHA256");
                mac_SHA256_pass.init(key_pass);

                String mensajeNonce_pass = "Mensaje enviado no integro." + nonce_pass;

                byte[] b_pass = mensajeNonce_pass.getBytes(StandardCharsets.UTF_8);
                byte[] digest_pass = mac_SHA256_pass.doFinal(b_pass);
                String digestHex_pass = Utiles.bytesToHex(digest_pass);

                // Habría que calcular el correspondiente MAC con la clave compartida por servidor/cliente
                System.err.println(digestHex_pass);

                // Importante para que el mensaje se envíe
                System.err.flush();
            }
            outputStream.close();
            bufferedReader.close();
            socket.close();

        } while (true);
    }

}
