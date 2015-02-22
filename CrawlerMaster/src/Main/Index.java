package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import Main.Handler.OnAsyncTaskListener;
import model.Command;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Index extends JFrame{
	
	private SocketServer socketServer;
	private JTextField sendText;
	private JButton sendBtn;
	private JTextArea textArea;
	private JButton startBtn;
	public Index() {
		try {
			//默认绑定9090端口
			this.socketServer = new SocketServer(9090);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initComponents();
		bindFrameEvent();
		bindAsyncEvent();
	}
	
	private void initComponents(){
		getContentPane().setLayout(null);
		setBounds(400, 100, 800, 600);
		
		sendBtn = new JButton("发送");
		sendBtn.setBounds(200, 64, 117, 29);
		getContentPane().add(sendBtn);
		
		sendText = new JTextField();
		sendText.setColumns(10);
		sendText.setBounds(28, 63, 153, 28);
		getContentPane().add(sendText);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(28, 105, 524, 277);
		getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		startBtn = new JButton("启动服务器");
		startBtn.setBounds(200, 23, 117, 29);
		getContentPane().add(startBtn);
	}
	
	public static void main(String[] args){
		Index frame = new Index();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void bindFrameEvent(){
		
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				socketServer.start();
			}
		});
		
		sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = sendText.getText();
				if(msg.equals("")){
					msg = "Hello slave, I am master!";
				}
				socketServer.sendAll(new Command(Command.CMD_MSG, msg));
				msg = "";
			}
		});
	}
	
	private void bindAsyncEvent(){
		socketServer.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onReceive(Handler handler, Command command) {
				switch (command.getType()) {
				case Command.CMD_MSG:
					String text = textArea.getText();
					String cmdStr = "";
					cmdStr += handler.getServerId() + "--type";
					cmdStr += command.getType() + " info:" + command.getInfo();
					text = text + cmdStr + "\n";
					textArea.setText(text);
					textArea.setCaretPosition(text.length());
					
					break;

				default:
					break;
				}
			}
			
			@Override
			public void onClose(String slaveId) {
				System.out.println("slave closed:" + slaveId);
			}
		});
	}
}
