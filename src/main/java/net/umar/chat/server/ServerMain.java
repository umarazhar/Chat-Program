package net.umar.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import net.umar.chat.data.DataHandler;

public class ServerMain {
	
	private static ServerSocket serverSocket;
	private static Server server;

	public static void main(String[] args) throws IOException{
		
		serverSocket = new ServerSocket(8990);
		server = new Server();
		
		server.start();
		
		while (true) {
			
			Socket newConnection = serverSocket.accept();
			
			String name = null;
			
			while (name == null) {
				System.out.println("No name yet!");
				name = DataHandler.readLine(newConnection.getInputStream());
			}
			
			System.out.println("In Server Main class!");
			
			server.addClient(newConnection, name);
		}
		
	}
}
