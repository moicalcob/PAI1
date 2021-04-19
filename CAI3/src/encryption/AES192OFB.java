package encryption;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AES192OFB {
	private static final String clave = "ClaveST22-CAI3@1";
    private static final String iv = "0123456789ABCDEF"; // vector de inicialización

    public static String runAES192OFB() {
        try {
            File f = new File("./imagenes/shutterstock_392691841.jpeg");
            String encodedImage = encodeFileToBase64Binary(f);

            long startTime = System.nanoTime();
            String encrypted_AES_192_OFB = AES192OFB.encryptAES192OFBEncrypt(clave, iv, encodedImage);
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            System.out.println("Tiempo de encriptado: " + timeElapsed / 1000000 + "ms");

            startTime = System.nanoTime();
            String decrypted_AES_CBC_128 = AES192OFB.decryptAES192OFB(clave, iv, encrypted_AES_192_OFB);
            endTime = System.nanoTime();
            timeElapsed = endTime - startTime;
            System.out.println("Tiempo de desencriptado: " + timeElapsed / 1000000 + "ms");

            System.out.println("Imagen se conserva de manera íntegra: " + encodedImage.equals(decrypted_AES_CBC_128));
            return encrypted_AES_192_OFB;
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "";
        }
    }

    
    
    public static String encryptAES192OFBEncrypt(String key, String iv, String cleartext) throws Exception {
    	// Definición del tipo de algoritmo a utilizar (AES, DES, RSA)
        String alg = "AES";
        // Definición del modo de cifrado a utilizar
        String cI = "AES/OFB/PKCS5Padding";
    	Cipher cipher = Cipher.getInstance(cI);
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), alg);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(cleartext.getBytes());
        return new String(encodeBase64(encrypted));
    }

    public static String decryptAES192OFB(String key, String iv, String encrypted) throws Exception {
        // Definición del tipo de algoritmo a utilizar (AES, DES, RSA)
        String alg = "AES";
        // Definición del modo de cifrado a utilizar
        String cI = "AES/OFB/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(cI);
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), alg);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        byte[] enc = decodeBase64(encrypted);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(enc);
        return new String(decrypted);
    }

    private static String encodeFileToBase64Binary(File file) throws Exception {
        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        fileInputStreamReader.read(bytes);
        return new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8);
    }

}
