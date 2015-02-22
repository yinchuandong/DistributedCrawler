package Main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import Main.Handler.OnAsyncTaskListener;
import model.Command;

public class SocketClient {
	/**
	 * 开启任务
	 */
	public static final int CMD_START = 1000;
	/**
	 * 分发任务
	 */
	public static final int CMD_DISPATCH_TASK = 1001;
	/**
	 * 停止任务
	 */
	public static final int CMD_STOP = 1002;
	/**
	 * 回收爬取的文件
	 */
	public static final int CMD_RECALL_FILE = 1003;
	/**
	 * 暂停任务
	 */
	public static final int CMD_PAUSE = 1004;
	/**
	 * 重启任务
	 */
	public static final int CMD_RESTART = 1005;
	
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
