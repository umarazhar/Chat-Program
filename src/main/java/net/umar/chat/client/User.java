package net.umar.chat.client;

import net.umar.chat.data.DataHandler;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class User {
	
	private final String IP = "107.170.72.159";
	
	private String name;
	private ArrayList<String> friends;
	
	private boolean online;
	
	private Socket socket;
	
	public User(String name) {
		this.name = name;
		this.friends = new ArrayList<String>();
		
		this.online = false;
	}
	
	public void connect() throws UnknownHostException, IOException {
		if (!online) {
			socket = new Socket(IP, 8990);
			DataHandler.writeLine(socket.getOutputStream(), name);
			online = true;
		}
	}
	
	public void sendMessage(String message, String recipient) throws IOException {
		String toSend = "Send Message " + recipient + " " + message.getBytes().length;
		DataHandler.writeLine(socket.getOutputStream(), toSend);
		DataHandler.writeLine(socket.getOutputStream(), message);
	}
        public void sendFile(File file, String recipient) throws IOException {
            if (!file.isFile())
                return;
            Path path = file.toPath();
            byte[] data = Files.readAllBytes(path);
            String toSend = "Send File " + recipient + " " + data.length + " " + file.getAbsolutePath().split("\\.")[file.getAbsolutePath().split("\\.").length - 1];
            System.out.println(toSend);
            DataHandler.writeLine(socket.getOutputStream(), toSend);
            DataHandler.writeBytes(socket.getOutputStream(), data);
            
        }
	
	public ArrayList<String> getFriends() {
		return friends;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public String retrieveMessage() throws IOException {
//		System.out.println(online);
		if (online) {
//			System.out.println("Attempting to retrieve message...");
			return DataHandler.readLine(socket.getInputStream());
//			if (DataHandler.streamReady(socket.getInputStream())) {
//				String tmp = DataHandler.readAll(socket.getInputStream());
//				return tmp;
//			}
		}
		
		return null;
	}
	
	public byte[] retrieveData() {
		if (online) {
			String message = null;
			
			while (message == null) {
				try {
					message = DataHandler.readLine(socket.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return message.getBytes();
		}
		
		return null;
	}
	
	public byte[] retrieveData(int bytes) throws IOException {
		if (online) {
			byte[] message = null;
			int i = 0;
                        byte[] tmp;
			while (message == null) {
				try {
					tmp = DataHandler.readBytes(socket.getInputStream(), bytes);
                                        
					if (tmp != null) {
						message = tmp;
                                                System.out.println("Bytes received");
                                        }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return message;
		}
		
		return null;
	}
	
}