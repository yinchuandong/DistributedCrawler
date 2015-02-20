package Main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import Main.Handler.OnAsyncTaskListener;
import model.Command;

public class SocketServer {

	private int port = 9090;
	private ServerSocket serverSocket = null;
	private ExecutorService threadPool = null;
	
	/**
	 * 从机的socketMap
	 */
	private HashMap<String, Handler> slaveMap = null;
	
	private OnAsyncTaskListener onAsyncTaskListener = null;
	
	private boolean isRunning = false;
	
	public SocketServer(int port) throws IOException{
		this.port = port;
		this.threadPool = Executors.newCachedThreadPool();
		this.serverSocket = new ServerSocket(port);
		this.slaveMap = new HashMap<String, Handler>();
	}
	
	/**
	 * 设置监听器，用来接受命令
	 * @param onAsyncTaskListener
	 */
	public void setOnAsyncTaskListener(OnAsyncTaskListener onAsyncTaskListener){
		this.onAsyncTaskListener = onAsyncTaskListener;
	}
	
	/**
	 * 开启服务器监听，判断是否有从机加入
	 */
	public void start(){
		isRunning = true;
		
		new Thread(){
			public void run(){
				while(isRunning){
					try {
						Socket socket = serverSocket.accept();
						Handler handler = new Handler(socket);
						handler.setOnAsyncTaskListener(onAsyncTaskListener);
				
						InetAddress address = socket.getInetAddress();
						String key = address.getHostAddress() + ":" + socket.getPort();
						
						System.out.println("从机的id:" + key);
						
						//将slave机加到map中
						slaveMap.put(key, handler);
						
						threadPool.execute(handler);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	
}
