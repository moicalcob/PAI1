package main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GenerateKeys {

    public static void generateKeys() throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        Base64.Encoder encoder = Base64.getEncoder();

        Key pub = kp.getPublic();
        Key pvt = kp.getPrivate();

        String outFile = "keys";
        Writer out = new FileWriter(outFile + ".key");
        out.write("-----BEGIN RSA PRIVATE KEY-----\n");
        out.write(encoder.encodeToString(pvt.getEncoded()));
        out.write("\n-----END RSA PRIVATE KEY-----\n");
        out.close();

        out = new FileWriter(outFile + ".pub");
        out.write("-----BEGIN RSA PUBLIC KEY-----\n");
        out.write(encoder.encodeToString(pub.getEncoded()));
        out.write("\n-----END RSA PUBLIC KEY-----\n");
        out.close();
    }

    public static void main(String[] args) {
        try {
            try {
                generateKeys();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
