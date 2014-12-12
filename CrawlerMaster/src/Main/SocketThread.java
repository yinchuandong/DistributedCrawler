package Main;

import java.net.Socket;

import javax.swing.JFrame;

import model.Command;
import Main.Handler.OnAsyncTaskListener;

/**
 * 每一个从机对应的线程， 在这里对进行命令的控制
 * @author wangjiewen
 *
 */
public class SocketThread extends Thread{
	private Handler handler = null;
	
	public SocketThread(Handler handler) {
		this.handler = handler;
		this.init();
	}
	
	/**
	 * 有界面的情况，自己重载
	 * @param frame
	 * @param handler
	 */
	public SocketThread(JFrame frame, Handler handler){
		this(handler);
	}
	
	private void init(){
		bindEvent();
	}
	
	/**
	 * 绑定handler的事件
	 */
	private void bindEvent(){
		handler.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onReceive(Socket socket, Command command) {
				System.out.println(command.getType() + "-" + command.getInfo());
				handler.send(new Command(1, "Hello slave, I am master, onreceive"));
			}

			@Override
			public void onClose(String slaveId) {
				
			}
		});
	}
	
}