package net.umar.chat.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server extends Thread implements Runnable{
	
	private HashMap<String, Client> clients;
	
	public Server() {
		clients = new HashMap<String, Client>();
	}
	
	public void addClient(Socket socket, String name) {
		if (clients.containsKey(name))
			return;
		
		System.out.println("Adding client: " + name);
		Client tmpClient = new Client(socket, name, clients);
		clients.put(name, tmpClient);
		tmpClient.start();
		System.out.println("Client " + name + " successfully added!");
		
	}

	@Override
	public void run() {
		
		while (true) {
			
		}
		
	}

}
