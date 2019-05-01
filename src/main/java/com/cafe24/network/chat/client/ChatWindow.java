package com.cafe24.network.chat.client;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatWindow {

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;

	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;
	
	class ChatClientThread implements Runnable {
		@Override
		public void run() {
			try {
				while(true) {
					String msg = br.readLine();
					if("EXIT".equals(msg)) {
						break;
					}
					else if("MESSAGE".equals(msg.split(":")[0])) {
						updateTextArea(msg.split(":")[1]);
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(socket != null && socket.isClosed() == false) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ChatWindow(String name, Socket socket) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		
		try {
			this.socket = socket;
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			this.pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void finish() {
		pw.println("EXIT");
		try {
			if(socket != null && socket.isClosed() == false) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
	public void show() {
		new Thread(new ChatClientThread()).start();
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if(keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		frame.add(BorderLayout.CENTER, textArea);

		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				finish();
			}
		});
		frame.setVisible(true);
		frame.pack();
		
		//thread 생성
	}
	
	private void updateTextArea(String message) {
		textArea.append(message);
		textArea.append("\n");
	}
	
	private void sendMessage() {
		String message = textField.getText();
		textField.setText("");
		textField.requestFocus();
		
		pw.println("MESSAGE:" + message);
	}
}
