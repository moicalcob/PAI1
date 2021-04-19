package encryption;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

public class BLOWFISH {

    private static final String clave = "ClaveST22-CAI3@1";

    public static String runBLOWFISH() {
        try {
            File f = new File("./imagenes/shutterstock_392691841.jpeg");
            String encodedImage = encodeFileToBase64Binary(f);

            long startTime = System.nanoTime();
            String encrypted_BLOWFISH = BLOWFISH.encryptBLOWFISH(clave, encodedImage);
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            System.out.println("Tiempo de encriptado: " + timeElapsed / 1000000 + "ms");

            startTime = System.nanoTime();
            String decrypted_BLOWFISH = BLOWFISH.decryptBLOWFISH(clave, encrypted_BLOWFISH);
            endTime = System.nanoTime();
            timeElapsed = endTime - startTime;
            System.out.println("Tiempo de desencriptado: " + timeElapsed / 1000000 + "ms");

            System.out.println("Imagen se conserva de manera íntegra: " + encodedImage.equals(decrypted_BLOWFISH));
            return encrypted_BLOWFISH;
        } catch (Exception error) {
            System.out.println(error.getMessage());
            return "";
        }
    }

    public static String encryptBLOWFISH(String key, String cleartext) throws Exception {
        String alg = "Blowfish";
        String cI = "Blowfish";
        Cipher cipher = Cipher.getInstance(cI);
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), alg);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(cleartext.getBytes());
        return new String(encodeBase64(encrypted));
    }

    public static String decryptBLOWFISH(String key, String encrypted) throws Exception {
        String alg = "Blowfish";
        String cI = "Blowfish";
        Cipher cipher = Cipher.getInstance(cI);
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), alg);
        byte[] enc = decodeBase64(encrypted);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
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
