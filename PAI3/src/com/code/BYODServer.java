package com.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class BYODServer {
	
	private SSLServerSocket serverSocker; 
	private static final String[] protocols = new String[] {"TLSv1.3"};
	private static final String[] chiperSuits = new String[] {"TLS_AES_128_GCM_SHA256"};
	
	
	public BYODServer() throws Exception { 
		SSLServerSocketFactory sockFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		this.serverSocker = (SSLServerSocket) sockFactory.createServerSocket(8080);
		serverSocker.setEnabledProtocols(protocols);
		serverSocker.setEnabledCipherSuites(chiperSuits);
	}
	
	public void runServer() throws IOException { 
		while(true) {
			//Esperamos hasta que recibimos alguna petición desde el cliente
			try { 
				System.err.println("Esperando conexiones por parte de los clientes");
				Socket socket = (Socket) serverSocker.accept();
				
				//Abrimos un BufferReader para leer los datos de los clientes
				BufferedReader inputServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				// Abrimos un printWritter para enviar los datos a los clientes 
				PrintWriter outputServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				String username = inputServer.readLine();
				String password = inputServer.readLine();
				String messageClient = inputServer.readLine();
				
				Boolean userValid = false; 
				FileReader linesToRead = new FileReader("users.txt");
				
				try (BufferedReader br = new BufferedReader(linesToRead)) {
					String line; 
					while ((line = br.readLine()) != null) { 
						if(line.equals(username.concat("||").concat(password))) { 
							userValid = true; 
						}
					}
				}
				
				if(!userValid) { 
					outputServer.println("Lo siento, según la solución del Security Team 22 su nombre de usuario o contraseña no son correctos, intentelo de nuevo");
				} else { 
					File archivo = new File("messages.txt");
					BufferedWriter bWriter = new BufferedWriter(new FileWriter(archivo, true));
					bWriter.write(messageClient + "\n");
					bWriter.close();
					outputServer.println("Su mensaje ha sido almacenado correctamente. Muchas gracias por parte del Security Team 22");			
				}
				outputServer.close();
				inputServer.close();
				socket.close();
 			}catch (SSLHandshakeException exception) {
 				//Exception provocada por un handshake9
 				System.err.println("Error: " + exception);
 			}catch (IOException e) {
				// Exception causado por IOException
 				System.err.println("Error: " + e);
			}
		}
	}
    // Programa principal
    public static void main(String args[]) throws Exception {
        BYODServer server = new BYODServer();
        server.runServer();
    }
}
