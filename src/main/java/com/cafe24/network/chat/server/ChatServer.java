package com.cafe24.network.chat.server;

import java.io.IOException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private static final int PORT = 8890;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		List<Writer> clients = new ArrayList<Writer>();
		
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));
			log("server starts");
			
			while(true) {
				Socket socket = serverSocket.accept();
				
				new Thread(new ChatServerThread(socket, clients)).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void log(String message) {
		System.out.println("[server] " + message);
	}
}
