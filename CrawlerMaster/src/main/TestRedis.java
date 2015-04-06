package main;

import redis.clients.jedis.Jedis;
import util.RedisUtil;

public class TestRedis {

	public static void main(String[] args){
		Jedis jedis = RedisUtil.getInstance();
		String name = jedis.get("name2");
		System.out.println(name);
		System.out.println(jedis.keys("*").size());
	}
}
