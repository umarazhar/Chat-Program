package net.umar.chat.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.umar.chat.data.DataHandler;

/**
 * Client class holds all the information for a client including the socket,
 * name, and all friends!
 * @author Umar
 */
public class Client extends Thread implements Runnable {

    private boolean running = true;

    private HashMap<String, Client> clients; //all clients online
    private HashMap<String, Client> friends; //all clients online who are friends with this client

    private Socket socket; //socket of this user
    private String name; //name of user

    public Client(Socket socket, String name, HashMap<String, Client> clients) {
        this.socket = socket;

        this.name = name;

        this.clients = clients;
        
        try {
            init();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Reads the users friends file and adds all online friends to users friends
     * list. Then sends the friends file to the user.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void init() throws FileNotFoundException, IOException {
        String filename = name+"/friends.txt";
        Scanner scanner = new Scanner(new BufferedReader(new FileReader(filename)));
        
        System.out.println("Loading friends list!");
        while (scanner.hasNextLine()) {
            String name = scanner.nextLine().trim();
            Client tmp = clients.get(name);
            if (tmp != null) {
                friends.put(name, tmp);
            }
        }
        
        System.out.println("Friends list laoded!");
        
        scanner.close();
        
        File tmpFile = new File(filename);
        Path path = tmpFile.toPath();
        byte[] bytes = Files.readAllBytes(path);
        
        String message = "Receive FriendList " + this.name + " " + bytes.length;
        
        DataHandler.writeLine(this.socket.getOutputStream(), message);
        
        System.out.println("Sending friends list!");
        
        DataHandler.writeBytes(this.socket.getOutputStream(), bytes);
        
        System.out.println("Friends list sent to " + this.name + "!");
    }

    /**
     * Reads the input stream of this clients socket to check if any request has
     * been sent.
     * @return a string with the request or null if no request
     */
    public String getRequest() {
        try {
            return DataHandler.readLine(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Returns this clients socket.
     * @return Socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Takes a request as a string and processes the request. Action performed
     * based on the request.
     * @param request the request to be processed
     */
    public void processRequest(String request) {
        String[] line = request.split(" ");

        //if something is being sent such as a message or file
        if (line[0].trim().equals("Send")) {
            if (line[1].trim().equals("Message")) { //if a message is being sent
                String name = line[2].trim();

                //gets the client object of the person this file has to be sent
                Client recipient = clients.get(name);

                if (recipient != null) {
                    //a message to the recipient in the format:
                    //Receive Message (person sending the file) (number of bytes being sent)
                    String message = "Receive " + "Message " + this.name + " " + line[3];
                    try {
                        System.out.println("Sending " + name + " message!");
                        //sends the recipient a message indicating that they are about to receive a message
                        DataHandler.writeLine(recipient.getSocket().getOutputStream(), message);
                        System.out.println("Message sent to " + name);

                        String received = null;

                        //continues to loop until message to be sent is received
                        while (received == null) {
                            byte[] tmp = DataHandler.readBytes(this.socket.getInputStream(), Integer.parseInt(line[3]));
                            if (tmp != null) {
                                received = new String(tmp);
                            }
                        }

                        System.out.println("Message received: " + received);

                        System.out.println("Sending message to " + name);

                        //sends the message to the recipient
                        DataHandler.writeLine(recipient.getSocket().getOutputStream(), received);

                        System.out.println("Message sent to " + name);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {//if person was not found in list then send a message back to sender of message
                    //indicating that the recipient is not online.
                    //format: Failure Send Offline (name of recipient)
                    String message = "Failure Send Offline " + name;

                    try {
                        DataHandler.writeLine(this.socket.getOutputStream(), message);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } else if (line[1].trim().equals("File")) {//if a file is being sent
                String name = line[2].trim(); //name of the recipient of the file

                //gets the client object of the recipient
                Client recipient = clients.get(name);

                if (recipient != null) {
                    //message to be sent to recipient prior to sending file.
                    //Format: Receive File (name of sender) (number of bytes to be sent) (file extension)
                    String message = "Receive " + "File " + this.name + " " + line[3] + " " + line[4];
                    try {
                        System.out.println("Sending " + name + " file!");
                        //sends the message to the recipient
                        DataHandler.writeLine(recipient.getSocket().getOutputStream(), message);
                        System.out.println("File sent to " + name);

                        //keeps looping until all the bytes of the file are read
                        int size = Integer.parseInt(line[3]);
                        byte[] received = new byte[size];
                        int i = 0;
                        while (i < size) {
                            byte[] tmp = DataHandler.readBytes(this.socket.getInputStream(), 1);
                            if (tmp != null) {
                                received[i++] = tmp[0];
                            }
                        }

                        System.out.println("File received: " + received);

                        System.out.println("Sending file to " + name);

                        //sends all the received bytes to the user
                        DataHandler.writeBytes(recipient.getSocket().getOutputStream(), received);

                        System.out.println("File sent to " + name);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    //if recipient is not in list then send user message indicating offline status
                    //Format: Failure Send Offline (recipient name)
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
            if (request != null) {
                this.processRequest(request);
            }
        }
    }
}
