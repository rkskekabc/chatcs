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
	
	Thread thread;
	
	class ChatClientThread implements Runnable {
		@Override
		public void run() {
			try {
				while(true) {
					String msg = br.readLine();
					if("EXIT".equals(msg.split(":")[0])) {
						System.out.println("client exit");
						break;
					}
					if("MESSAGE".equals(msg.split(":")[0])) {
						//메시지 디코딩 처리
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

	public ChatWindow(String name, Socket socket, BufferedReader br, PrintWriter pw) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		
		// 인자로 받은 닉네임과 소켓으로 통신
		this.socket = socket;
		this.br = br;
		this.pw = pw;
		
	}
	
	public void show() {
		//thread 생성
		Thread thread = new Thread(new ChatClientThread());
		thread.start();
		
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
		
	}

	// 창을 닫으면 EXIT 신호를 보내고 종료
	private void finish() {
		try {
			pw.println("EXIT");
			// 스레드가 종료될 때까지 기다림
			thread.join();
			
			if(socket != null && socket.isClosed() == false) {
				// 스레드에서 받고있는 input 스트림을 먼저 닫아서 종료시킴
				//br.close();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
	private void updateTextArea(String message) {
		textArea.append(message);
		textArea.append("\n");
	}
	
	private void sendMessage() {
		String message = textField.getText();
		pw.println("MESSAGE:" + message);
		
		textField.setText("");
		textField.requestFocus();
	}
}
