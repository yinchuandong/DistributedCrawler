package Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;

import Main.Handler.OnAsyncTaskListener;
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

public class Index extends JFrame{
	
	private SocketServer socketServer;
	private DefaultTableModel tableModel;
	private Map<String, Integer> slaveIdMap;
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
		
		dispatchBtn = new JButton("分配url");
		dispatchBtn.setBounds(355, 23, 117, 29);
		getContentPane().add(dispatchBtn);
		
		pauseCrawlBtn = new JButton("暂停");
		pauseCrawlBtn.setBounds(484, 64, 117, 29);
		getContentPane().add(pauseCrawlBtn);
		
		JButton removeBtn = new JButton("移除从机");
		removeBtn.setBounds(484, 23, 117, 29);
		getContentPane().add(removeBtn);
		
		doCrawlBtn = new JButton("开始爬取");
		doCrawlBtn.setBounds(355, 64, 117, 29);
		getContentPane().add(doCrawlBtn);
		
		withdrawBtn = new JButton("回收文件");
		withdrawBtn.setBounds(613, 23, 117, 29);
		getContentPane().add(withdrawBtn);
	}
	
	public static void main(String[] args){
		Index frame = new Index();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initData(){
		tableModel = new DefaultTableModel();
		String header[] = new String[] { "Status", "IP", "Port", };
	    tableModel.setColumnIdentifiers(header);
	    table.setModel(tableModel);
	    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	    table.getColumnModel().getColumn(0).setMinWidth(0);
	    table.getColumnModel().getColumn(0).setMaxWidth(40);
	    table.getColumnModel().getColumn(0).setPreferredWidth(40);
	    
	    slaveIdMap = new HashMap<String, Integer>();
	   
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
		//绑定从机监听事件
		socketServer.setOnAsyncTaskListener(new OnAsyncTaskListener() {
			
			@Override
			public void onAccept(String slaveId, Handler handler) {
				System.out.println(handler.getInetAddress());
				tableModel.addRow(new Object[] { false, handler.getSocket().getInetAddress(), handler.getSocket().getPort()});
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
		
		
		//分配url
		dispatchBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isChecked = (boolean) tableModel.getValueAt(0, 0);
			    System.out.println(isChecked);
			}
		});
	}
	
	
	private void dispatchUrl(){
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
