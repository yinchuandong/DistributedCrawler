package ConsistentHash;


public class ServerNode{
	
	private String ip;
	private int port;
	public ServerNode(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public String toString(){
		return ip + ":" + port;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.toString().equals(this.toString());
	}
	
	
	
}
