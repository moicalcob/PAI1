package com.st22;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ServerSocketFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {
   
	protected static final String SecretKey = "Secret-ST-22";
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
            
            byte[] nonceMessageByte = nonceMessage.getBytes("UTF-8");
            
            byte[] digest = this.mac_cliente_SHA256.doFinal(nonceMessageByte);
            
            String macMensajeCalculadoServ = Utiles.bytesToHex(digest);
            
            //Checkeamos el nonce
            boolean nonceValid = Utiles.nonceIsValid(nonce);
            if (nonceValid) {
                Utiles.storeNonce(nonce);
                System.out.println("Nonce v·lido");
                System.out.println("Cifrado hex: "+macMensajeDelCliente);
            } else {
                Utiles.messageVerificationFailed(message);
                System.out.println("Nonce no v·lido");
            }
            
            if(macMensajeDelCliente.equals(macMensajeCalculadoServ) && nonceValid) {
            	System.out.println("Mensaje enviado integramente");
            	File fw = new File("./nonces.txt");
            	BufferedWriter bw = new BufferedWriter(new FileWriter(fw,true));
            	bw.append(nonce);
            	bw.newLine();
            	bw.close();
            	
            	serverOutput.println(message);
            	
            	//Construccion del Nonce de respuesta del servidor
            	byte[] nonceBytes = new byte[32];
                SecureRandom rand = SecureRandom.getInstance("SHA1PRNG","SUN");
                rand.nextBytes(nonceBytes);
                String noncePass = NonceGenerator.convertBytesToHex(nonceBytes);
                serverOutput.println(noncePass);

                // Generar la Key apartir de la clave secreta entre servidor y cliente
                SecretKeySpec keyPass = new SecretKeySpec(SecretKey.getBytes(), "HmacSHA256");

                // Cosntruimos la nueva mac
                this.mac_cliente_SHA256 = Mac.getInstance("HmacSHA256");
                this.mac_cliente_SHA256.init(keyPass);

                String mensajeNoncePass = "Mensaje enviado integro " + noncePass;

                // get the string as UTF-8 bytes
                byte[] bPass = mensajeNoncePass.getBytes("UTF-8");

                // create a digest from the byte array
                byte[] digestPass = this.mac_cliente_SHA256.doFinal(bPass);

                String digestHexPass = Utiles.bytesToHex(digestPass);
                
                // Habr√≠a que calcular el correspondiente MAC con la clave compartida por servidor/cliente
                serverOutput.println(digestHexPass);
                // Importante para que el mensaje se envie
                serverOutput.flush();
            }else { 
            	System.err.println("Mensaje enviado no esta integro");
            	
            	File fw = new File("./nonces.txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(fw, true));
                Date date = new Date();
                bw.append("ERROR: " + date + "\n" + "Integrity message has been failure. Message: " + message);
                bw.newLine();
                bw.close();
                
            	//Construccion del Nonce de respuesta del servidor
            	byte[] nonceBytes = new byte[32];
                SecureRandom rand = SecureRandom.getInstance("SHA1PRNG","SUN");
                rand.nextBytes(nonceBytes);
                String nonce_pass = NonceGenerator.convertBytesToHex(nonceBytes);
                serverOutput.println(nonce_pass);

                // Generar la Key apartir de la clave secreta entre servidor y cliente
                SecretKeySpec key_pass = new SecretKeySpec(SecretKey.getBytes(), "HmacSHA256");

                //Cosntuimos la mac con el mensaje no integro interceptado
                final Mac mac_SHA256_pass = Mac.getInstance("HmacSHA256");
                mac_SHA256_pass.init(key_pass);

                String mensajeNonce_pass = "Mensaje enviado no integro." + nonce_pass;

                // get the string as UTF-8 bytes
                byte[] b_pass = mensajeNonce_pass.getBytes("UTF-8");

                // create a digest from the byte array
                byte[] digest_pass = mac_SHA256_pass.doFinal(b_pass);

                String digestHex_pass = Utiles.bytesToHex(digest_pass);

                // Habr√≠a que calcular el correspondiente MAC con la clave compartida por servidor/cliente
                System.err.println(digestHex_pass);

                // Importante para que el mensaje se env√≠e
                System.err.flush();
            }
            outputStream.close();
            bufferedReader.close();
            socket.close();

        } while (true);
    }
    
    //LOS METODOS AUXILIARES ESTAN EN UNA CLASE UTILES PARA PODER REAPROVECHARLOS EN EL CLIENTE Y NO DUPLICAR CODIGO BRO
}
