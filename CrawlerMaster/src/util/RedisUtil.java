package util;

import org.apache.commons.collections.functors.IfClosure;

import redis.clients.jedis.Jedis;

public class RedisUtil {
	static Jedis jedis = null;

	
	private RedisUtil(){
		
	}
	
	public static Jedis getInstance(){
		return getInstance("127.0.0.1", 6379);
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
