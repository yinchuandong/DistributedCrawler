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

public class SocketThreadPool {

	private int port = 9090;
	private ServerSocket serverSocket = null;
	private ExecutorService threadPool = null;
	
	/**
	 * 从机的socketMap
	 */
	private HashMap<String, Handler> slaveMap = null;
	
	private boolean isRunning = false;
	
	public SocketThreadPool(int port) throws IOException{
		this.port = port;
		this.threadPool = Executors.newCachedThreadPool();
		this.serverSocket = new ServerSocket(port);
		this.slaveMap = new HashMap<String, Handler>();
	}
	
	/**
	 * 有界面的情况下自己重载吧
	 * @param jFrame
	 * @param port
	 * @throws IOException
	 */
	public SocketThreadPool(JFrame jFrame, int port) throws IOException{
		this(port);
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
				
						InetAddress address = socket.getInetAddress();
						String key = address.getHostAddress() + ":" + socket.getPort();
						
						System.out.println("从机的id:" + key);
						
						//将slave机加到map中
						slaveMap.put(key, handler);
						
						threadPool.execute(new SocketThread(handler));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public static void main(String[] args) throws IOException{
		SocketThreadPool threadPool = new SocketThreadPool(9090);
		threadPool.start();
		System.out.println("启动");
	}
	
}
