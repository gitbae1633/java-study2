package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

public class ChatClientThread extends Thread{

	private BufferedReader bufferedReader;
	
	public ChatClientThread(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	@Override
	public void run() {
	    /* reader를 통해 읽은 데이터 콘솔에 출력하기 (message 처리) */
		//3. 요청 처리 			
		while( true ) {
		    String request = null;
			try {
				request = bufferedReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log("socket closed");
//				e.printStackTrace();
				break;
			}
			
		   if( request == null ) {
		      log( "서버로 부터 연결 끊김" );
		      break;
		   }
		   
		   // message print
		   log(request);
		}
	}
	
	public void log(String msg) {
		System.out.println(msg);
	}

}
