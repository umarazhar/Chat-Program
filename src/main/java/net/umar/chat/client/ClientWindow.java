package net.umar.chat.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class ClientWindow extends JFrame implements Runnable {

    private final int WIDTH = 250;
    private final int HEIGHT = 400;

    private User user;

    private HashMap<String, ChatWindow> windows = new HashMap<String, ChatWindow>();

    private JPanel panel;

    private JPanel friend_panel;

    private JMenuBar menu_bar;

    private Box main_box;
    private Box friend_box;
    private Box button_box;

    public ClientWindow() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        this.setSize(WIDTH, HEIGHT);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }

        String name = JOptionPane.showInputDialog("Enter your desired name: ");

        user = new User(name);

        init();

        this.setJMenuBar(menu_bar);

        this.add(panel);

        this.setVisible(true);
    }

    public ClientWindow(String name) {
        this.setSize(WIDTH, HEIGHT);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        user = new User(name);

        init();

        this.setJMenuBar(menu_bar);

        this.add(panel);

        this.setVisible(true);
    }

    private void init() {

        menu_bar = new JMenuBar();

        JMenu file_menu = new JMenu("File");
        JMenuItem connect_item = new JMenuItem("Connect");

        connect_item.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            user.connect();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
        );

        file_menu.add(connect_item);
        menu_bar.add(file_menu);

        panel = new JPanel();

        main_box = new Box(BoxLayout.Y_AXIS);
        friend_box = new Box(BoxLayout.Y_AXIS);
        button_box = new Box(BoxLayout.X_AXIS);

        Dimension friend_size = new Dimension(WIDTH - 20, HEIGHT - 100);

        friend_box.setMaximumSize(friend_size);
        friend_box.setPreferredSize(friend_size);
        friend_box.setMinimumSize(friend_size);

        friend_panel = new JPanel();

        JScrollPane scroll_friend_pane = new JScrollPane(friend_panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        friend_box.add(scroll_friend_pane);

        JLabel upcoming = new JLabel("Coming soon!");

        final JLabel warnings = new JLabel("");

        friend_panel.add(upcoming);
        friend_panel.add(warnings);

        JButton chat_button = new JButton("Chat");

        chat_button.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!user.isOnline()) {
                            warnings.setText("Please connect to online server first!");
                            return;
                        }
                        String friend = JOptionPane.showInputDialog("Who would you like to chat with?");
                        ChatWindow newChat = new ChatWindow(user, user.getName(), friend);
                        windows.put(friend, newChat);
                    }
                }
        );

        button_box.add(chat_button);

        main_box.add(friend_box);
        main_box.add(button_box);

        panel.add(main_box);

    }

    public void update() {
        try {
            String message = user.retrieveMessage();
            if (message != null) {
                String[] line = message.split(" ");

                if (line[0].trim().equals("Receive")) {
                    if (line[1].trim().equals("Message")) {
                        String newMessage = new String(user.retrieveData(Integer.parseInt(message.split(" ")[3].trim())));
                        String tmpName = line[2].trim();
                        ChatWindow tmpWindow = windows.get(tmpName);
                        if (tmpWindow != null) {
                            if (tmpWindow.isVisible()) {
                                windows.get(tmpName).addMessage(tmpName + ": " + newMessage);
                            } else {
                                windows.remove(tmpName);
                            }
                        } else {
                            windows.put(tmpName, new ChatWindow(user, user.getName(), tmpName));
                            windows.get(tmpName).addMessage(tmpName + ": " + newMessage);
                        }
                        System.out.println("Message received from: " + message.split(" ")[2]);
                        System.out.println("Message: " + newMessage);
                    } else if (line[1].trim().equals("File")) {
                        System.out.println("Incoming File!");
                        byte[] newFile = user.retrieveData(Integer.parseInt(message.split(" ")[3].trim()));
                        String tmpName = line[2].trim();
                        ChatWindow tmpWindow = windows.get(tmpName);
                        if (tmpWindow != null) {
                            if (tmpWindow.isVisible()) {
                                windows.get(tmpName).addMessage(tmpName + ": Received new file!");
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.showSaveDialog(null);
                                if (fileChooser.getSelectedFile() != null) {
                                    String filename = fileChooser.getSelectedFile().getAbsolutePath() + "." + line[4];
                                    FileOutputStream fileOut = new FileOutputStream(filename);
                                    fileOut.write(newFile);
                                    fileOut.close();
                                }
                            } else {
                                windows.remove(tmpName);
                            }
                        } else {
                            windows.put(tmpName, new ChatWindow(user, user.getName(), tmpName));
                            windows.get(tmpName).addMessage(tmpName + ": Received new file!");
                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.showSaveDialog(null);
                            if (fileChooser.getSelectedFile() != null) {
                                String filename = fileChooser.getSelectedFile().getAbsolutePath() + "." + line[4];
                                FileOutputStream fileOut = new FileOutputStream(filename);
                                fileOut.write(newFile);
                                fileOut.close();
                            }
                        }
                        System.out.println("Message received from: " + message.split(" ")[2]);
                        System.out.println("Message: " + newFile);
                    }
                } else if (line[0].trim().equals("Failure")) {
                    if (line[1].trim().equals("Send")) {
                        if (line[2].trim().equals("Offline")) {
                            String tmpName = line[3].trim();
                            ChatWindow tmpWindow = windows.get(tmpName);
                            if (tmpWindow != null && tmpWindow.isVisible()) {
                                windows.get(tmpName).addMessage(tmpName + " is not online! Message was not send.");
                            } else {
                                windows.remove(tmpName);
                            }

                        }

                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClientWindow window;
                try {
                    window = new ClientWindow();
                    
                    new Thread(window).start();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClientWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(ClientWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(ClientWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ClientWindow.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

    }

    public static void delay(long sec) {
        try {
            Thread.sleep(sec);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
//            System.out.println("hello");
            this.update();
            delay(100);
        }

    }

}
