package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;

import model.Command;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import base.BaseCrawler;
import crawler.BaiduHotelCrawler;
import crawler.BaiduSceneCrawler;
import distribute.Handler;
import distribute.SocketClient;
import distribute.Handler.OnAsyncTaskListener;

public class Index extends JFrame{
	private SocketClient socketClient;
	private ArrayList<String> urlList;
	private BaseCrawler baiduCrawler;
	
	
	private JTextField ipText;
	private JButton connetBtn;
	private JTextField sendText;
	private JButton sendBtn;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	private JTextField portText;
	public Index() {
		initComponents();
		initData();
		bindFrameEvent();
	}
	
	private void initComponents(){
		getContentPane().setLayout(null);
		setBounds(400, 100, 800, 600);
		
		connetBtn = new JButton("连接");
		connetBtn.setBounds(200, 7, 117, 29);
		getContentPane().add(connetBtn);
		
		ipText = new JTextField();
		ipText.setText("192.168.233.42");
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
	
	private void initData(){
		urlList = new ArrayList<String>();
		baiduCrawler = new BaiduSceneCrawler();
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
					socketClient = new SocketClient(masterIp, port);
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
				socketClient.send(new Command(Command.CMD_MSG, msg));
				msg = "";
			}
		});
	}
	
	private void bindAsyncEvent(){
		// listening server socket
		socketClient.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onReceive(Handler handler, Command cmd) {
				switch (cmd.getType()) {
				case Command.CMD_MSG:
					String text = textArea.getText();
					String cmdStr = "";
					cmdStr += handler.getServerId() + "--type";
					cmdStr += cmd.getType() + " info:" + cmd.getInfo();
					text = text + cmdStr + "\n";
					textArea.setText(text);
					textArea.setCaretPosition(text.length());
					
					//根据信息判断初始化哪个爬虫
					if(cmd.getInfo().startsWith("#scene")){
						baiduCrawler = new BaiduSceneCrawler();
						System.out.println("-----------------reinit BaiduSceneCrawler-----------");
					}else if (cmd.getInfo().startsWith("#hotel")) {
						baiduCrawler = new BaiduHotelCrawler();
						System.out.println("-----------------reinit BaiduHotelCrawler-----------");

					}
					
					break;
					
				case Command.CMD_DISPATCH_TASK:
					//分发url
					urlList.add(cmd.getInfo());
					System.out.println("url:" + cmd.getInfo());
					break;
					
				case Command.CMD_WRITE_URL:
					//url分发完毕，并写入文件中,初始化爬虫
					writeUrl();
					baiduCrawler.initSeeds();
					break;
					
				case Command.CMD_START:
					//开启爬虫
					baiduCrawler.start();
					System.out.println("start:" + cmd.getInfo());
					break;
					
				case Command.CMD_PAUSE:
					//暂停爬虫
					baiduCrawler.pause();
					break;
					
				default:
					break;
				}
			}
			
			@Override
			public void onClose(String masterId) {
				System.out.println("master closed:" + masterId);
			}
		});
	}
	
	
	private void writeUrl(){
		try {
			File seedsFile = null;
			if(baiduCrawler instanceof BaiduSceneCrawler){
				seedsFile = new File("data/baidu-scene.txt");
			}else if (baiduCrawler instanceof BaiduHotelCrawler) {
				seedsFile = new File("data/baidu-hotel.txt");
			}
			PrintWriter writer = new PrintWriter(seedsFile);
			for (String url : urlList) {
				writer.println(url);
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
