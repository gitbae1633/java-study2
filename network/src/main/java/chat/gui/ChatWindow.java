package chat.gui;

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

public class ChatWindow {
	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter; 
	private String name;

	public ChatWindow(String name, BufferedReader bufferedReader,PrintWriter printWriter) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		this.name = name;
		this.bufferedReader = bufferedReader;
		this.printWriter = printWriter;
		new ChatClientThread(bufferedReader).start();
	}

	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if (keyCode == KeyEvent.VK_ENTER) {
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
			@Override
			public void windowClosing(WindowEvent e) {
				finish();
			}
		});
		frame.setVisible(true);
		frame.pack();

		// IOStream 받아오기

		// ChatClientThread 생성하고 실행
	}

	private void finish() {
		// quit protocol 구현
		// exit java(Application)
		System.exit(0);
		
		try {
			if (ChatClientApp.socket != null && !ChatClientApp.socket.isClosed()) {
				ChatClientApp.socket.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMessage() {
		String message = textField.getText();
		System.out.println("메세지 보내는 프로토콜 구현!!:" + message);

		textField.setText("");
		textField.requestFocus();

		if("quit".equals(message)) {
			finish();
		} else if(!"".equals(message)) {
			printWriter.println("message:" + message);
		}
		
		
		
		
		// ChatClientThread 에서 서버로 부터 받은 메세지가 있다 치고~~~
//		updateTextArea(name + ":" + message);
	}

	private void updateTextArea(String message) {
		textArea.append(message);
		textArea.append("\n");
	}

	private class ChatClientThread extends Thread {

		private BufferedReader bufferedReader;

		public ChatClientThread(BufferedReader bufferedReader) {
			this.bufferedReader = bufferedReader;
		}

		@Override
		public void run() {
			/* reader를 통해 읽은 데이터 콘솔에 출력하기 (message 처리) */
			// 3. 요청 처리
			String request = null;

			while (true) {
				try {
					request = bufferedReader.readLine();
					// message print
					log(request);
					updateTextArea(request);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log("socket closed");
//					e.printStackTrace();
					break;
				}

				if (request == null) {
					log("서버로 부터 연결 끊김");
					break;
				}

			}
		}

		public void log(String msg) {
			System.out.println(msg);
		}
	}
}