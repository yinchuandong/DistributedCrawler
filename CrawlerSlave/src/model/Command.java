package model;

import java.io.Serializable;

public class Command implements Serializable{
	
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
