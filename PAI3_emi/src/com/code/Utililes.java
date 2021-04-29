package com.code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utililes {
	
	public static void main(String[] args) throws IOException {
		 File archivoUsers = new File("users.txt");
		 BufferedWriter bufWriter = new BufferedWriter(new FileWriter(archivoUsers));
		 try {
			 for (int i = 0; i<= 300; i++) {
				 bufWriter.write("user"+ i + "||" + "myPassword"+i + "\n");
		 }

		 }catch (IOException e) {
			 System.out.println("Se ha producido un error escribiendo users en el archivo "+archivoUsers+ ".El error es "+e);
		 }finally {
			 bufWriter.close();
		 }
		 
	}
		
	
}
