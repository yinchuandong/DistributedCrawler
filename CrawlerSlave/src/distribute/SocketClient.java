package distribute;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import distribute.Handler.OnAsyncTaskListener;
import model.Command;

public class SocketClient {
	
	private String masterIp = "127.0.0.1";
	private int masterPort = 9090;
	
	private Socket serverSocket = null;
	private Handler handler = null;
	
	public SocketClient(){
		
	}
	public SocketClient(String masterIp, int masterPort) throws UnknownHostException, IOException{
		this.masterIp = masterIp;
		this.masterPort = masterPort;
		this.serverSocket = new Socket(masterIp, masterPort);
		this.handler = new Handler(serverSocket);
	}
	
	public void setMaster(String masterIp, int masterPort){
		this.masterIp = masterIp;
		this.masterPort = masterPort;
	}
	
	/**
	 * 开启监听
	 */
	public void start(){
		new Thread(this.handler).start();
	}
	
	/**
	 * 设置监听器，用来接受命令回调
	 * @param onAsyncTaskListener
	 */
	public void setOnAsyncTaskListener(OnAsyncTaskListener onAsyncTaskListener){
		handler.setOnAsyncTaskListener(onAsyncTaskListener);
	}
	
	/**
	 * 发送数据
	 * @param command
	 * @return
	 */
	public boolean send(Command command) {
		return handler.send(command);
	}
	
	public Handler getHandler(){
		return handler;
	}
	
	
}
