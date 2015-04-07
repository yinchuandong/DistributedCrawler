package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;

import ConsistentHash.ConsistentHash;
import ConsistentHash.HashFunction;
import ConsistentHash.ServerNode;
import model.Command;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import distribute.Handler;
import distribute.SocketServer;
import distribute.Handler.OnAsyncTaskListener;

public class Index extends JFrame{
	//--------functional variables-------
	private SocketServer socketServer;
	private DefaultTableModel tableModel;
	private Map<String, Integer> slaveIdMap;
	private ArrayList<String> seedsList;
	private ArrayList<String> selectedSlaveList;
	private ConsistentHash<ServerNode> consistentHash;
	
	//--------UI components--------
	private JTextField sendText;
	private JButton sendBtn;
	private JTextArea textArea;
	private JButton startBtn;
	private JScrollPane scrollPane_1;
	private JTable table;
	private JButton dispatchBtn;
	private JButton pauseCrawlBtn;
	private JButton doCrawlBtn;
	private JButton withdrawBtn;
	private JButton writeUrlBtn;
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
		initData();
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
		scrollPane.setBounds(28, 105, 289, 277);
		getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		startBtn = new JButton("启动服务器");
		startBtn.setBounds(200, 23, 117, 29);
		getContentPane().add(startBtn);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(365, 106, 365, 277);
		getContentPane().add(scrollPane_1);
		
		table = new JTable(){
			
			@Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
		};
		scrollPane_1.setViewportView(table);
		
		dispatchBtn = new JButton("1.分配url");
		dispatchBtn.setBounds(355, 23, 117, 29);
		getContentPane().add(dispatchBtn);
		
		pauseCrawlBtn = new JButton("暂停");
		pauseCrawlBtn.setBounds(484, 64, 117, 29);
		getContentPane().add(pauseCrawlBtn);
		
		JButton removeBtn = new JButton("移除从机");
		removeBtn.setBounds(613, 64, 117, 29);
		getContentPane().add(removeBtn);
		
		doCrawlBtn = new JButton("3.开始爬取");
		doCrawlBtn.setBounds(613, 23, 117, 29);
		getContentPane().add(doCrawlBtn);
		
		withdrawBtn = new JButton("回收文件");
		withdrawBtn.setBounds(355, 64, 117, 29);
		getContentPane().add(withdrawBtn);
		
		writeUrlBtn = new JButton("2.完成url分发");
		writeUrlBtn.setBounds(484, 23, 117, 29);
		getContentPane().add(writeUrlBtn);
	}
	
	public static void main(String[] args){
		Index frame = new Index();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initData(){
		//initialize table model
		tableModel = new DefaultTableModel();
		String header[] = new String[] { "Status", "IP", "Port", };
	    tableModel.setColumnIdentifiers(header);
	    table.setModel(tableModel);
	    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	    table.getColumnModel().getColumn(0).setMinWidth(0);
	    table.getColumnModel().getColumn(0).setMaxWidth(40);
	    table.getColumnModel().getColumn(0).setPreferredWidth(40);
	    
	    //initialize slaveId map
	    slaveIdMap = new HashMap<String, Integer>();
	    consistentHash = new ConsistentHash<ServerNode>(new HashFunction(), 1000);
	    seedsList = new ArrayList<String>();
	    selectedSlaveList = new ArrayList<String>();
	}
	
	private void bindFrameEvent(){
		
		//启动服务器
		startBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				socketServer.start();
			}
		});
		
		//发送消息
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
		

		//1.分配url
		dispatchBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadSlave();
				loadSeeds();
				dispatchUrl();
			}
		});
		
		//2.write url
		writeUrlBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Command cmd = new Command(Command.CMD_WRITE_URL, "write url to  data/seed.txt");
				for (String slaveId : selectedSlaveList) {
					socketServer.send(slaveId, cmd);
				}
			}
		});
		
		//3.开启爬虫
		doCrawlBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Command cmd = new Command(Command.CMD_START, "start crawling");
				for (String slaveId : selectedSlaveList) {
					socketServer.send(slaveId, cmd);
				}
			}
		});
		
		pauseCrawlBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Command cmd = new Command(Command.CMD_PAUSE, "pause crawling");
				for (String slaveId : selectedSlaveList) {
					socketServer.send(slaveId, cmd);
				}
			}
		});
	}
	
	private void bindAsyncEvent(){
		//listening slave socket
		socketServer.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onAccept(String slaveId, Handler handler) {
				System.out.println(handler.getInetAddress());
				tableModel.addRow(new Object[] { true, handler.getSocket().getInetAddress().getHostAddress(), handler.getSocket().getPort()});
				slaveIdMap.put(slaveId, slaveIdMap.size());
			}
			
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
				socketServer.removeSlave(slaveId);
				tableModel.removeRow(slaveIdMap.get(slaveId));
				slaveIdMap.remove(slaveId);
				System.out.println("slave closed:" + slaveId);
			}

		});
		
	}
	
	/**
	 * 加载选中的slave
	 */
	private void loadSlave(){
		//select the checked slave node
		int row = tableModel.getRowCount();
		consistentHash.clear();
		selectedSlaveList.clear();
		for (int i = 0; i < row; i++) {
			boolean isSlected = (boolean)tableModel.getValueAt(i, 0);
			if(isSlected){
				String ip = (String)tableModel.getValueAt(i, 1);
				int port = (int)tableModel.getValueAt(i, 2);
				ServerNode node = new ServerNode(ip, port);
				consistentHash.add(node);
				selectedSlaveList.add(node.toString());
			}
		}
	}
	
	/**
	 * 加载种子文件
	 */
	private void loadSeeds(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("./data/baidu.txt")));
			String buff = null;
			while((buff = reader.readLine()) != null){
				seedsList.add(buff);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 分发url给选中的从机
	 */
	private void dispatchUrl(){
		for (String surl : seedsList) {
			ServerNode node = consistentHash.get(surl);
			String slaveId = node.toString();
			Command cmd = new Command(Command.CMD_DISPATCH_TASK, surl);
			socketServer.send(slaveId, cmd);
			System.out.println(slaveId);
		}
	}
}
