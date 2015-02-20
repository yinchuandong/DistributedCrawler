package Main;

import java.io.IOException;

import Main.Handler.OnAsyncTaskListener;
import model.Command;

public class Index {
	
	public static void main(String[] args){
		
	}
	
	public static void start(){
	
		try {
			SocketServer server = new SocketServer(8008);
			server.setOnAsyncTaskListener(new OnAsyncTaskListener() {
				
				@Override
				public void onReceive(Handler handler, Command command) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onClose(String slaveId) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
