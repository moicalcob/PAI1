package com.st22.etm;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;


public class ChaCha20Poly1305 {

    private static final String ENCRYPT_ALGO = "ChaCha20-Poly1305";
    private static final int NONCE_LEN = 12;

    public byte[] encrypt(byte[] pText, SecretKey key) throws Exception {
        return encrypt(pText, key, getNonce());
    }

    public byte[] encrypt(byte[] pText, SecretKey key, byte[] nonce) throws Exception {

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

        IvParameterSpec iv = new IvParameterSpec(nonce);

        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] encryptedText = cipher.doFinal(pText);

        return ByteBuffer.allocate(encryptedText.length + NONCE_LEN)
                .put(encryptedText)
                .put(nonce)
                .array();
    }

    public byte[] decrypt(byte[] cText, SecretKey key) throws Exception {

        ByteBuffer bb = ByteBuffer.wrap(cText);

        byte[] encryptedText = new byte[cText.length - NONCE_LEN];
        byte[] nonce = new byte[NONCE_LEN];
        bb.get(encryptedText);
        bb.get(nonce);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

        IvParameterSpec iv = new IvParameterSpec(nonce);

        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        return cipher.doFinal(encryptedText);

    }

    private static byte[] getNonce() {
        byte[] newNonce = new byte[12];
        new SecureRandom().nextBytes(newNonce);
        return newNonce;
    }

}

