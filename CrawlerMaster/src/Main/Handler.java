package Main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import model.Command;

/**
 * 每一个handler对应一个slave
 * @author wangjiewen
 *
 */
public class Handler {

	private Socket socket = null;

	private InetAddress inetAddress = null;
	
	/**
	 * 从机在SocketThreadPool中的key
	 */
	private String slaveId = null;

	private OnAsyncTaskListener onAsyncTaskListener = null;

	public Handler(Socket socket) {
		this.socket = socket;
		this.inetAddress = socket.getInetAddress();
		this.slaveId = inetAddress.getHostAddress() + ":" + socket.getPort();

		// this.init();
		runReceive();
	}

	private void init() {
		System.out.println(inetAddress.getHostAddress());
	}

	/**
	 * 发送数据
	 * 
	 * @param command
	 * @return
	 */
	public boolean send(Command command) {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(
					new BufferedOutputStream(socket.getOutputStream()));

			outputStream.writeObject(command);
			outputStream.flush();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 设置监听器，进行回调
	 * 
	 * @param onAsyncTaskListener
	 */
	public void setOnAsyncTaskListener(OnAsyncTaskListener onAsyncTaskListener) {
		this.onAsyncTaskListener = onAsyncTaskListener;
	}

	/**
	 * 进行接收，开启另一个线程
	 */
	private void runReceive() {
		new Thread() {
			public void run() {
				try {
					ObjectInputStream inputStream = new ObjectInputStream(
							socket.getInputStream());

					Object obj = null;
					
					while ((obj = inputStream.readObject()) != null) {
						Command command = (Command) obj;
						if (onAsyncTaskListener != null) {
							onAsyncTaskListener.onReceive(socket, command);
						}
						
						//解决eof异常
						try{
							inputStream = new ObjectInputStream(socket.getInputStream());
						}catch(EOFException e){
							continue;
						}
					}
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally{
					if(socket != null){
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
				onAsyncTaskListener.onClose(slaveId);
				System.out.println("master close");
			}
		}.start();

	}

	public interface OnAsyncTaskListener {

		/**
		 * 接收到命令的时候调用
		 * 
		 * @param command
		 */
		public void onReceive(Socket socket, Command command);
		
		public void onClose(String slaveId);
	}
}
