package Main;

import java.io.IOException;

import Main.Handler.OnAsyncTaskListener;
import model.Command;

public class Index {
	
	public static void main(String[] args){
		try {
			SocketClient client = new SocketClient();
			client.setOnAsyncTaskListener(new OnAsyncTaskListener() {
				
				@Override
				public void onReceive(Handler handler, Command command) {
					
				}
				
				@Override
				public void onClose(String slaveId) {
					
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
