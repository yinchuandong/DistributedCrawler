package util;


import java.io.File;

import redis.clients.jedis.Jedis;

public class RedisUtil {
	static Jedis jedis = null;

	
	private RedisUtil(){
		
	}
	
	public static Jedis getInstance(){
		if(jedis == null){
			String[] connstr = PageUtil.readFile(new File("./redis.conf")).split(":");
			String ip = connstr[0];
			int port = Integer.parseInt(connstr[1]);
			jedis = new Jedis(ip, port);
		}
		return jedis;
	}
	
	public static Jedis getInstance(String ip, int port){
		if(jedis == null){
			jedis = new Jedis(ip, port);
		}
		return jedis;
	}
	
	public static void close(){
		if(jedis != null){
			jedis.disconnect();
			jedis = null;
		}
	}
}
