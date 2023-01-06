package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
	public static void main(String[] args) {
		
		final int PORT = 4000;
		Scanner scanner = null;
		Socket socket = null;
		
		try {
		   //1. 키보드 연결
			scanner = new Scanner(System.in);

		   //2. socket 생성
			socket = new Socket();

		   //3. 연결
			SocketAddress address = new InetSocketAddress("127.0.0.1", PORT);
			socket.connect(address);

		   //4. reader/writer 생성
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( socket.getInputStream(), StandardCharsets.UTF_8 ) );
			PrintWriter printWriter = new PrintWriter( new OutputStreamWriter( socket.getOutputStream(), StandardCharsets.UTF_8 ), true );

		   //5. join 프로토콜
		   System.out.print("닉네임>>");
		   String nickname = scanner.nextLine();
		   printWriter.println( "join:" + nickname );
		   printWriter.flush();

		   //6. ChatClientReceiveThread 시작
		   Thread thread = new ChatClientThread( bufferedReader );
		   thread.start();

		   //7. 키보드 입력 처리
		   while( true ) {
		      System.out.print( ">>" );
		      String input = scanner.nextLine();
						
		      if( "quit".equals( input ) == true ) {
		          // 8. quit 프로토콜 처리
				  printWriter.println( "quit:" + nickname );
				  printWriter.flush();
		          break;
		      } else {
		          // 9. 메시지 처리 
				  printWriter.println( "message:" + input );
				  printWriter.flush();
		      }
		   }

		} catch( IOException ex ) {
		       log( "error:" + ex );
		} finally {
		      //10. 자원정리
	    	  try {if (socket != null && !socket.isClosed()) {
					socket.close();
				}
				if (scanner != null) {
					scanner.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public static void log(String msg) {
		System.out.println(msg);
	}
}