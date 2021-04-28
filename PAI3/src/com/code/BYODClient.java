package com.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

public class BYODClient {
	
	private static final String[] protocols = new String[] {"TLSv1.3"};
	private static final String[] chiperSuits = new String[] {"TLS_AES_128_GCM_SHA256"};
	

	public BYODClient() throws IOException { 
		try {
			SSLSocketFactory factorySock = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket clientSocket = (SSLSocket) factorySock.createSocket("localhost", 8080);
			clientSocket.setEnabledProtocols(protocols);
			clientSocket.setEnabledCipherSuites(chiperSuits);
			
			//Creamos un PrintWriter que nos permita enviar el mensaje o MAC al servidor. 
			PrintWriter outputClient = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			
			// Ahora diseñamos el panel para que el Usuario introduzca su username, contraseña y el mensaje que desee
			
			String username = JOptionPane.showInputDialog(null, "Introduce tu nombre de usuario");
			outputClient.println(username);
			
			String password = JOptionPane.showInputDialog(null, "Introduce tu contraseña");
			outputClient.println(password);
			
			String clientMessage = JOptionPane.showInputDialog(null, "Introduce tu mensaje");
			outputClient.println(clientMessage);
			
			outputClient.flush();
			
			// Apartir de aquí creamos un BufferReader para poder leer la respuesta que nos proporciona nuestro servidor.
			
			BufferedReader inputClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String serverResponse = inputClient.readLine();
			
			JOptionPane.showMessageDialog(null, serverResponse);
			
			outputClient.close();
			inputClient.close();
			clientSocket.close();
			
		}finally { 
			System.exit(0);
		}
	}
	
	// ejecución del cliente de verificación de la integridad
	public static void main(String args[]) throws IOException {
		new BYODClient();
	}
}
