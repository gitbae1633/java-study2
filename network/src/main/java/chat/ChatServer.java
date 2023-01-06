package chat;

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	
	static List<Writer> listWriters = new ArrayList<Writer>();
	
	public static void main(String[] args) throws IOException {
		final int PORT = 4000;
		
		// 1. 서버 소겟 생성
		ServerSocket serverSocket = new ServerSocket();
					
		// 2. 바인딩
//		String hostAddress = InetAddress.getLocalHost().getHostAddress();
		String hostAddress = "127.0.0.1";
		serverSocket.bind( new InetSocketAddress( hostAddress, PORT ) );			
		log( "연결 기다림 " + hostAddress + ":" + PORT );

		// 3. 요청 대기 
		while( true ) {
		   Socket socket = serverSocket.accept();
//		   new ChatServerThread( socket ).start();
		   new ChatServerThread( socket, listWriters ).start();
			

		}

	}
	
	public static void log(String msg) {
		System.out.println(msg);
	}
	


}