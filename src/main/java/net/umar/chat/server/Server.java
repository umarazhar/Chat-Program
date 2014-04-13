package net.umar.chat.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Server class holds all the users currently connected to this server.
 * @author Umar
 */
public class Server extends Thread implements Runnable {

    private HashMap<String, Client> clients; //all users currently online

    /**
     * Constructor
     * Initializes the clients list
     */
    public Server() {
        clients = new HashMap<String, Client>();
    }

    /**
     * Adds a client to the server by adding them to the list of online users.
     * @param socket the socket through which to communicate with user
     * @param name name of user
     */
    public void addClient(Socket socket, String name) {
        if (clients.containsKey(name)) {
            return;
        }

        System.out.println("Adding client: " + name);
        Client tmpClient = new Client(socket, name, clients); //new client object holding all client information
        clients.put(name, tmpClient); //adds client to list of online users
        tmpClient.start(); //starts clients thread
        System.out.println("Client " + name + " successfully added!");

    }

    @Override
    public void run() {

        while (true) {

        }

    }

}
