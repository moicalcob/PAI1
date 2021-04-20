package com.st22;

import com.st22.etm.ChaCha20Poly1305;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Main {

    public static void main(String[] args) throws Exception {
        String input = "Encrypt-Then-Mac del equipo ST22 para la consultoría de INSEGUS";

        ChaCha20Poly1305 cipher = new ChaCha20Poly1305();

        SecretKey key = getKey();
        String macPrivateKey = "SecretoSt22";

        System.out.println("Input                  : " + input);

        System.out.println("\n---Encriptando---");
        byte[] cText = cipher.encrypt(input.getBytes(), key);
        String cypherText = convertBytesToHex(cText);
        System.out.println("Key               (hex): " + convertBytesToHex(key.getEncoded()));
        System.out.println("Encrypted         (hex): " + cypherText);

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec macKey = new SecretKeySpec(macPrivateKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(macKey);
        mac.update(cypherText.getBytes(StandardCharsets.UTF_8));
        byte[] finalMac = mac.doFinal();
        System.out.println("MAC               (hex): " + convertBytesToHex(finalMac));

        // ------------------------- ENVÍO DE DATOS AL OTRO EXTREMO -------------------------- //

        System.out.println("\n---Desencriptando---");
        System.out.println("Texto recibido    (hex): " + cypherText);
        System.out.println("MAC recibida      (hex): " + convertBytesToHex(finalMac));
        Mac macDecrypt = Mac.getInstance("HmacSHA1");
        SecretKeySpec macKeyDecrypt = new SecretKeySpec(macPrivateKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        macDecrypt.init(macKey);
        macDecrypt.update(cypherText.getBytes(StandardCharsets.UTF_8));
        byte[] finalMacDecrypt = macDecrypt.doFinal();
        System.out.println("MAC generada con texto recibido (hex): " + convertBytesToHex(finalMacDecrypt));
        byte[] pText = cipher.decrypt(cText, key);
        System.out.println("MAC recibida coincide con la generada, por tanto conserva integridad: " + convertBytesToHex(finalMacDecrypt).equals(convertBytesToHex(finalMac)));
        System.out.println("Desencriptado: " + new String(pText));
    }

    private static String convertBytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(String.format("%02x", temp));
        }
        return result.toString();
    }

    private static SecretKey getKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("ChaCha20");
        keyGen.init(256, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }

}
