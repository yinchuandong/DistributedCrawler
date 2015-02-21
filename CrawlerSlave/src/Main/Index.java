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
	private SocketClient socketClient;
	private JTextField ipText;
	private JButton connetBtn;
	private JTextField sendText;
	private JButton sendBtn;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JTextField portText;
	public Index() {
		initComponents();
		bindFrameEvent();
	}
	
	private void initComponents(){
		getContentPane().setLayout(null);
		setBounds(400, 100, 800, 600);
		
		connetBtn = new JButton("连接");
		connetBtn.setBounds(200, 7, 117, 29);
		getContentPane().add(connetBtn);
		
		ipText = new JTextField();
		ipText.setText("127.0.0.1");
		ipText.setBounds(28, 6, 102, 28);
		getContentPane().add(ipText);
		ipText.setColumns(10);
		
		sendBtn = new JButton("发送");
		sendBtn.setBounds(200, 64, 117, 29);
		getContentPane().add(sendBtn);
		
		sendText = new JTextField();
		sendText.setColumns(10);
		sendText.setBounds(28, 63, 153, 28);
		getContentPane().add(sendText);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(28, 117, 493, 265);
		getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		portText = new JTextField();
		portText.setText("9090");
		portText.setColumns(10);
		portText.setBounds(131, 6, 50, 28);
		getContentPane().add(portText);
	}
	
	public static void main(String[] args){
		Index frame = new Index();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	private void bindFrameEvent(){
		connetBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String masterIp = ipText.getText();
				int port = Integer.parseInt(portText.getText());
				if(masterIp.equals("")){
					masterIp = "127.0.0.1";
				}
				
				try {
					socketClient = new SocketClient("127.0.0.1", port);
					bindAsyncEvent();
					socketClient.start();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = sendText.getText();
				if(msg.equals("")){
					msg = "Hello master, I am slave!";
				}
				socketClient.send(new Command(2, msg));
				msg = "";
			}
		});
	}
	
	private void bindAsyncEvent(){
		System.out.println("bind success");
		socketClient.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onReceive(Handler handler, Command command) {
				System.out.println("123123");
				String text = textArea.getText();
				String cmdStr = "";
				cmdStr += handler.getSlaveId() + "--type";
				cmdStr += command.getType() + " info:" + command.getInfo();
				text = text + cmdStr + "\n";
				textArea.setText(text);
				textArea.setCaretPosition(text.length());
			}
			
			@Override
			public void onClose(String slaveId) {
				
			}
		});
	}
}
