package com.st22;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.SocketFactory;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;

public class Client {

    protected static final String SecretKey = "Secreto-ST-22";

    protected Mac mac_cliente_SHA256;

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

            // Generamos el nonce
            byte[] nonceBytes = new byte[32];
            SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
            rand.nextBytes(nonceBytes);
            String nonce = NonceGenerator.convertBytesToHex(nonceBytes);

            JTextField origin = new JTextField(5);
            JTextField target = new JTextField(5);
            JTextField quantity = new JTextField(5);

            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel("Cuenta origen:"));
            myPanel.add(origin);
            myPanel.add(Box.createHorizontalStrut(15)); // a spacer
            myPanel.add(new JLabel("Cuenta destino:"));
            myPanel.add(target);
            myPanel.add(Box.createHorizontalStrut(15)); // a spacer
            myPanel.add(new JLabel("Cantidad:"));
            myPanel.add(quantity);

            int result = JOptionPane.showConfirmDialog(null, myPanel,
                    "Por favor introduce la cuenta origen, la de destino y la cantidad", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                System.out.println("Origen: " + origin.getText());
                System.out.println("Destino: " + target.getText());
                System.out.println("Cantidad: " + quantity.getText());
            }

            String message = "FROM: " + origin.getText() + " - TO: " +target.getText() + " -> " + quantity.getText() + "€";

            //Construimos la clave con el SecretKey Cliente-Servidor
            SecretKeySpec key = new SecretKeySpec(SecretKey.getBytes(), "HmacSHA256");
            this.mac_cliente_SHA256 = Mac.getInstance("HmacSHA256");
            this.mac_cliente_SHA256.init(key);

            String nonceMessage = message + nonce;
            byte[] nonceMessageBytes = nonceMessage.getBytes(StandardCharsets.UTF_8);
            byte[] digest = this.mac_cliente_SHA256.doFinal(nonceMessageBytes);

            String digestHexadecimal = Utiles.bytesToHex(digest);
            clientOutput.println(message);
            clientOutput.println(nonce);
            clientOutput.println(digestHexadecimal);
            clientOutput.flush();


//-----------------------------------------------------------------------------------------------------------------------------
// MENSAJE RECIBIDO DESDE EL SERVIDOR Y SU RESPECTIVO PROCESAMIENTO
//-----------------------------------------------------------------------------------------------------------------------------

            BufferedReader inputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String mensajeRecibido = inputStream.readLine();
            String nonceRecibido = inputStream.readLine();
            String macRecibida = inputStream.readLine();

            this.mac_cliente_SHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec keyVerification = new SecretKeySpec(SecretKey.getBytes(), "HmacSHA256");
            this.mac_cliente_SHA256.init(keyVerification);


            String nonceMessageServer = "Mensaje enviado integro " + nonceRecibido;
            byte[] nonceMessageRecivedBytes = nonceMessageServer.getBytes(StandardCharsets.UTF_8);
            byte[] digestServer = this.mac_cliente_SHA256.doFinal(nonceMessageRecivedBytes);
            String messageMacCalculated = Utiles.bytesToHex(digestServer);

            FileReader noncesSaved = new FileReader("./nonce_client.txt");
            boolean nonceValid = true;
            try (BufferedReader br = new BufferedReader(noncesSaved)) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals(nonceRecibido)) {
                        nonceValid = false;
                    }
                }
            }

            //Si las "macs" son iguales y el nonce es valido es decir no ha sido utilizado con anterioridad se guarda este Nonce nuevo todo correcto
            if (macRecibida.equals(messageMacCalculated) && nonceValid) {
                JOptionPane.showMessageDialog(null, mensajeRecibido);

                File fw = new File("./nonce_client.txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(fw, true));
                bw.append(nonce);
                bw.newLine();
                bw.close();
            } else { //En caso contrario lo que se realiza es que se gauarda en un archivo de Logs la Fecha y el fallo en el mensaje.
                JOptionPane.showMessageDialog(null, mensajeRecibido);
                File fw = new File("./logFile_client.txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(fw, true));
                Date date = new Date();
                bw.append("ERROR: ").append(String.valueOf(date)).append("\n").append("Integrity message has been failure. Message: ").append(message);
                bw.newLine();
                bw.close();
            }
            clientOutput.close();
            inputStream.close();
            client.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
