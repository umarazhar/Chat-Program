package net.umar.chat.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatWindow extends JFrame {
	
	private final int WIDTH = 300;
	private final int HEIGHT = 350;
	
	private User user;
	
	private String username;
	private String friendName;
	
	private JPanel panel;
	
	private JButton send;
	private JButton attach;
	
	private JTextArea outBox;
	private JTextArea inBox;
	
	public ChatWindow(User user, String userName, String friendName) {
		this.setSize(WIDTH, HEIGHT);
		
		this.user = user;
		this.username = userName;
		this.friendName = friendName;
		
		init();
		
		this.add(panel);
		
		this.setVisible(true);
	}
	
	public void init() {
		panel = new JPanel();
		
		outBox = new JTextArea(3,15);
		inBox = new JTextArea(10,15);
		
		outBox.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
					send.doClick();
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		outBox.setLineWrap(true);
		inBox.setLineWrap(true);
		inBox.setEditable(false);
		
		JScrollPane in_v_scroll = new JScrollPane(inBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane out_v_scroll = new JScrollPane(outBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		send = new JButton("Send");
		attach = new JButton("Attach");
		
		send.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if (!outBox.getText().equals("")){
						try {
							user.sendMessage(outBox.getText(), friendName);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						inBox.append("You: " + outBox.getText() + "\n");
						outBox.replaceRange("", 0, outBox.getText().length());
					}
				}
			}
		);
		
		attach.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					JFileChooser tmp = new JFileChooser();
					tmp.showOpenDialog(null);
//					
                                        if (tmp.getSelectedFile() != null) {
                                            try {
                                                user.sendFile(tmp.getSelectedFile(), friendName);
                                            } catch (IOException ex) {
                                                Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            inBox.append("You: " + "Sending file" + "\n");
                                            outBox.setText("");
                                        }
				}
			}
		);
		
		in_v_scroll.setPreferredSize(new Dimension(WIDTH - 20, HEIGHT - 130));
		in_v_scroll.setMaximumSize(new Dimension(WIDTH - 20, HEIGHT - 130));
		in_v_scroll.setMinimumSize(new Dimension(WIDTH - 20, HEIGHT - 130));
		out_v_scroll.setPreferredSize(new Dimension(WIDTH - send.getWidth(), 75));
		out_v_scroll.setMaximumSize(new Dimension(WIDTH - 83, 75));
		out_v_scroll.setMinimumSize(new Dimension(WIDTH - 83, 75));
		
		Box main_box_layout = new Box(BoxLayout.Y_AXIS);
		Box out_box_layout = new Box(BoxLayout.X_AXIS);
		Box button_box_layout = new Box(BoxLayout.Y_AXIS);
		
		button_box_layout.setPreferredSize(new Dimension(send.getWidth(), send.getHeight()*2 + 20));
		button_box_layout.add(send);
		button_box_layout.add(attach);
//		button_box_layout.add(Box.createVerticalStrut(send.getHeight()));
		
		out_box_layout.setPreferredSize(new Dimension(WIDTH, out_v_scroll.getHeight()));
		out_box_layout.add(out_v_scroll);
		out_box_layout.add(button_box_layout);
		
		
		
		main_box_layout.setPreferredSize(new Dimension(WIDTH-20, HEIGHT-50));
		main_box_layout.add(in_v_scroll);
		main_box_layout.add(out_box_layout);
		
		panel.add(main_box_layout);
	}
	
	public void addMessage(String message) {
		inBox.append(message + "\n");
	}

}
