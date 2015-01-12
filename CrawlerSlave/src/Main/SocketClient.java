package Main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import Main.Handler.OnAsyncTaskListener;
import model.Command;

public class SocketClient {

	private String masterIp = "127.0.0.1";
	private int masterPort = 9090;
	
	private Socket serverSocket = null;
	private Handler handler = null;
	
	public SocketClient() throws UnknownHostException, IOException {
		this.serverSocket = new Socket(masterIp, masterPort);
		this.handler = new Handler(serverSocket);
		
		this.bindEvent();
	}
	
	public void start(){
		Command command = new Command(0, "hello master, I am slave1");
		this.handler.send(command);
		command = new Command(0, "hello master, I am slave2");
		this.handler.send(command);
	}
	
	private void bindEvent(){
		handler.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onReceive(Socket socket, Command command) {
				System.out.println(command.getType() + "-" + command.getInfo());
			}

			@Override
			public void onClose(String slaveId) {
				
			}
		});
	}
	
	public static void main(String[] args){
		try {
			SocketClient client = new SocketClient();
			client.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
