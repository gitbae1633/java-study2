package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatServerThread extends Thread {

	private String nickname;
	private Socket socket;
	List<Writer> listWriters;

//	public ChatServerThread( Socket socket ) {
//		this.socket = socket;
//	}

	public ChatServerThread(Socket socket, List<Writer> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
	}

	@Override
	public void run() {
		// 1. Remote Host Information

		// 2. 스트림 얻기
		BufferedReader bufferedReader = null;
		PrintWriter printWriter = null;

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
					true);

			// 3. 요청 처리
			while (true) {
				String request = null;
				request = bufferedReader.readLine();

				// 4. 프로토콜 분석
				String[] tokens = request.split(":");

				if ("join".equals(tokens[0])) {

					doJoin(tokens[1], printWriter);

				} else if ("message".equals(tokens[0])) {
					if (tokens.length == 2)
						doMessage(tokens[1]);
					else
						doMessage("");

				} else if ("quit".equals(tokens[0])) {
					doQuit(printWriter);
				} else {
					ChatServer.log("에러:알수 없는 요청(" + tokens[0] + ")");
				}
				if (request == null) {
					log("클라이언트로 부터 연결 끊김");
					doQuit(printWriter);
		      		break;
				}
			}
		} catch (SocketException e) {
			log("error:" + e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			log("error:" + e1);
		} finally {
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void doJoin(String nickName, Writer writer) {
		this.nickname = nickName;
		String data = nickName + "님이 참여하였습니다.";
		broadcast(data);

		/* writer pool에 저장 */
		addWriter(writer);

		// ack
		((PrintWriter) writer).println("join:ok");
		((PrintWriter) writer).flush();
	}

	private void addWriter(Writer writer) {
		synchronized (listWriters) {
			listWriters.add(writer);
		}
	}

	private void broadcast(String data) {
		synchronized (listWriters) {
			for (Writer writer : listWriters) {
				PrintWriter printWriter = (PrintWriter) writer;
				printWriter.println(data);
				printWriter.flush();
			}
		}
	}

	private void doMessage(String message) {
		/* 잘 구현 해 보기 */
		broadcast(nickname + ":" + message);
	}

	private void doQuit(Writer writer) {
		removeWriter(writer);

		String data = nickname + "님이 퇴장 하였습니다.";
		broadcast(data);
	}

	private void removeWriter(Writer writer) {
		/* 잘 구현 해보기 */
		synchronized (listWriters) {
			listWriters.remove(writer);
		}
	}

	public void log(String msg) {
		System.out.println(msg);
	}

}
