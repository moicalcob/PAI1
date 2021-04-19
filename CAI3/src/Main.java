import encryption.AES_CBC_128;
import encryption.BLOWFISH;
import encryption.AES192OFB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("1: Encriptar con BLOWFISH");
        System.out.println("2: Encriptar con AES_CBC_128");
        System.out.println("3: Encriptar con AES_OFB_192");
        System.out.println("0: Exit");

        while (true) {
            char input = in.next().toCharArray()[0];
            switch (input) {
                case '1':
                    System.out.println("Encriptando con BLOWFISH");
                    String encryptedBLOWFISHImage = BLOWFISH.runBLOWFISH();
                    writeEncryptedFile(encryptedBLOWFISHImage, "blowfish_image.txt");
                    break;
                case '2':
                    System.out.println("Encriptando con AES_CBC_128");
                    String encryptedAES_CBC_128Image = AES_CBC_128.runAES_CBC_128();
                    writeEncryptedFile(encryptedAES_CBC_128Image, "AES_CBC_128_image.txt");
                    break;
                case '3' : 
                	System.out.println("Encriptando con AES_OFB_192");
                	String encryptedAES_OFB_192Image = AES192OFB.runAES192OFB();
                	writeEncryptedFile(encryptedAES_OFB_192Image, "AES_OFB_192_image.txt");
                	break;
                case '0':
                    System.exit(0);
                    break;
                default:
            }
            break;
        }
    }

    private static void writeEncryptedFile(String encryptedContent, String fileName) {
        try {
            File file = new File("./" + fileName);
            FileWriter myWriter = new FileWriter("./" + fileName);
            myWriter.write(encryptedContent);
            myWriter.close();
            printFileSize("./" + fileName);
            if (file.createNewFile()) {
                System.out.println("Archivo encriptado guardado: " + file.getName());
            } else {
                System.out.println("Archivo actualizado correctamente.");
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error.");
            e.printStackTrace();
        }
    }

    private static void printFileSize(String fileName) {
        Path path = Paths.get(fileName);
        try {
            long bytes = Files.size(path);
            System.out.printf("Tamaño del archivo: %,d kilobytes%n", bytes / 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}