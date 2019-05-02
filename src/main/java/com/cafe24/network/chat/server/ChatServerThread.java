package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/*
 * JOIN:닉네임 (클라이언트)		<->	JOIN:OK / JOIN:FAILED (서버)
 * MESSAGE:채팅내용 (클라이언트)	<->	MESSAGE:채팅내용 (서버)
 * EXIT (클라이언트)			<->	EXIT (서버)
 */
public class ChatServerThread implements Runnable {
	String nickName;
	Socket socket;
	BufferedReader br;
	PrintWriter pw;
	List<Writer> clients;
	
	public ChatServerThread(Socket socket, List<Writer> clients) {
		this.socket = socket;
		this.clients = clients;
	}

	@Override
	public void run() {
		//클라이언트 정보 표시
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
		String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
		int remotePort = inetRemoteSocketAddress.getPort();
		ChatServer.log("connected by client[" + remoteHostAddress + ":" + remotePort + "]");
		
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
			String joinCheck = br.readLine();
			
			// JOIN 신호 확인
			if("JOIN".equals(joinCheck.split(":")[0])) {
				pw.println("JOIN:OK");
				this.nickName = joinCheck.split(":")[1];
				broadcast(this.nickName + "님이 입장하셨습니다.");
				join(pw);
				
				// MESSAGE 신호로 클라이언트와 통신 & EXIT 신호가 오면 종료 처리
				while(true) {
					String msg = br.readLine();
					
					//메시지 인코딩 처리
					if("MESSAGE".equals(msg.split(":")[0])) {
						String decodedMessage = msg.split(":")[1];
						broadcast(this.nickName + " > " + decodedMessage);
					}
					else if("EXIT".equals(msg)) {
						pw.println("EXIT");
						synchronized(clients) {
							clients.remove(pw);
						}
						broadcast(this.nickName + "님이 퇴장하셨습니다.");
						break;
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
					ChatServer.log("exited by client[" + remoteHostAddress + ":" + remotePort + "]");
					this.socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 서버에 접속한 모든 클라이언트에게 메시지 전송
	private void broadcast(String Message) {
		for(Writer writer : this.clients) {
			PrintWriter pw = (PrintWriter)writer;
			pw.println("MESSAGE:" + Message);
		}
	}
	
	// 클라이언트 접속 처리
	private void join(Writer writer) {
		synchronized(clients) {
			clients.add(writer);
		}
	}
}
