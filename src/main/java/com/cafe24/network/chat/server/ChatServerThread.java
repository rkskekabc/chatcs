package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;

public class ChatServerThread implements Runnable {
	String nickName;
	Socket socket;
	PrintWriter pw;
	List<Writer> clients;
	
	public ChatServerThread(Socket socket, List<Writer> clients) {
		this.socket = socket;
		this.clients = clients;
	}

	@Override
	public void run() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
			String joinCheck = br.readLine();
			System.out.println("[server] " + joinCheck);
			if("JOIN".equals(joinCheck.split(":")[0])) {
				System.out.println("[server] " + joinCheck.split(":")[0]);
				pw.println("JOIN:OK");
				this.nickName = joinCheck.split(":")[1];
				broadcast(this.nickName + "님이 입장하셨습니다.");
				join(pw);
				
				while(true) {
					System.out.println("[server] " + this.nickName);
					String msg = br.readLine();
					System.out.println("[server] " + msg);
					if("EXIT".equals(msg.split(":")[0])) {
						pw.println("EXIT");
						synchronized(clients) {
							clients.remove(pw);
						}
						broadcast(this.nickName + "님이 퇴장하셨습니다.");
						break;
					}
					else if("MESSAGE".equals(msg.split(":")[0])) {
						broadcast(this.nickName + " > " + msg.split(":")[1]);
					}
				}
			} else {
				pw.println("JOIN:FAILED");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(this.socket != null && this.socket.isClosed() == false) {
					this.socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void broadcast(String Message) {
		for(Writer writer : this.clients) {
			PrintWriter pw = (PrintWriter)writer;
			pw.println("MESSAGE:" + Message);
		}
	}
	
	private void join(Writer writer) {
		synchronized(clients) {
			clients.add(writer);
		}
	}
}
