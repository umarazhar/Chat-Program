package net.umar.chat.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import net.umar.chat.data.DataHandler;

public class Client extends Thread implements Runnable{
	
	private boolean running = true;
	
	private HashMap<String, Client> clients;
	
	private Socket socket;
	private String name;

	public Client(Socket socket, String name, HashMap<String, Client> clients) {
		this.socket = socket;
		
		this.name = name;
		
		this.clients = clients;
		
	}
	
	public String getRequest() {
		try {
			return DataHandler.readLine(socket.getInputStream());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void processRequest(String request) {
		String[] line = request.split(" ");
		
		if (line[0].trim().equals("Send")) {
			if (line[1].trim().equals("Message")) {
				String name = line[2].trim();
				
				Client recipient = clients.get(name);
				
				if (recipient != null) {
					String message = "Receive " + "Message " + this.name + " " + line[3];
					try {
						System.out.println("Sending " + name + " message!");
						DataHandler.writeLine(recipient.getSocket().getOutputStream(), message);
						System.out.println("Message sent to " + name);
						
						String received = null;
						
						while (received == null) {
							byte[] tmp = DataHandler.readBytes(this.socket.getInputStream(), Integer.parseInt(line[3]));
							if (tmp != null)
								received = new String(tmp);
						}
						
						System.out.println("Message received: " + received);
						
						System.out.println("Sending message to " + name);
						
						DataHandler.writeLine(recipient.getSocket().getOutputStream(), received);
						
						System.out.println("Message sent to " + name);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				else {
					String message = "Failure Send Offline " + name;
					
					try {
						DataHandler.writeLine(this.socket.getOutputStream(), message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
                        else if (line[1].trim().equals("File")) {
                            String name = line[2].trim();

                            Client recipient = clients.get(name);

                            if (recipient != null) {
                                    String message = "Receive " + "File " + this.name + " " + line[3] + " " + line[4];
                                    try {
                                            System.out.println("Sending " + name + " file!");
                                            DataHandler.writeLine(recipient.getSocket().getOutputStream(), message);
                                            System.out.println("File sent to " + name);

                                            byte[] received = null;

                                            while (received == null) {
                                                    byte[] tmp = DataHandler.readBytes(this.socket.getInputStream(), Integer.parseInt(line[3]));
                                                    if (tmp != null)
                                                            received = tmp;
                                            }

                                            System.out.println("File received: " + received);

                                            System.out.println("Sending file to " + name);

                                            DataHandler.writeBytes(recipient.getSocket().getOutputStream(), received);

                                            System.out.println("File sent to " + name);

                                    } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                    }


                            }
                            else {
                                    String message = "Failure Send Offline " + name;

                                    try {
                                            DataHandler.writeLine(this.socket.getOutputStream(), message);
                                    } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                    }
                            }
                    }
		}
                
	}
	
	public void run() {
		while (running) {
			String request = this.getRequest();
			if (request != null)
				this.processRequest(request);
		}
	}
}
