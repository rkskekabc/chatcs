package com.cafe24.network.chat.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientApp {
	private static final String SERVER_IP = "192.168.1.17";
	private static final int PORT = 8890;

	public static void main(String[] args) {
		String name = null;
		Scanner scanner = new Scanner(System.in);

		while( true ) {
			
			System.out.println("대화명을 입력하세요.");
			System.out.print(">>> ");
			name = scanner.nextLine();
			
			if (name.isEmpty() == false ) {
				break;
			}
			
			System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
		}
		
		//1. 소켓 만들고
		Socket socket = new Socket();
		//2. iostream
		try {
			socket.connect(new InetSocketAddress(SERVER_IP, PORT));
			
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
			pw.println("JOIN:" + name);
	
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			String joinCheck = br.readLine();
	
			//3. join 성공
			if("JOIN".equals(joinCheck.split(":")[0]) && "OK".equals(joinCheck.split(":")[1])) {
				new ChatWindow(name, socket, br, pw).show();
			} else {
				System.out.println("접속 실패");
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}

}
