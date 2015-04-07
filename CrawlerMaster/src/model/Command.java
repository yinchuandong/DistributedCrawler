package model;

import java.io.Serializable;

public class Command implements Serializable{
	/**
	 * 开启任务
	 */
	public static final int CMD_START = 1000;
	/**
	 * 分发任务
	 */
	public static final int CMD_DISPATCH_TASK = 1001;
	/**
	 * 停止任务
	 */
	public static final int CMD_STOP = 1002;
	/**
	 * 回收爬取的文件
	 */
	public static final int CMD_RECALL_FILE = 1003;
	/**
	 * 暂停任务
	 */
	public static final int CMD_PAUSE = 1004;
	/**
	 * 重启任务
	 */
	public static final int CMD_RESTART = 1005;
	/**
	 * 发送消息
	 */
	public static final int CMD_MSG = 1006;
	/**
	 * 写入url到文件
	 */
	public static final int CMD_WRITE_URL = 1007;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int type;
	private String info;
	
	public Command(int type, String info) {
		this.type = type;
		this.info = info;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	

}
