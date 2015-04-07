package main;

import redis.clients.jedis.Jedis;
import util.RedisUtil;

public class TestRedis {

	public static void main(String[] args){
		Jedis jedis = RedisUtil.getInstance();
		String name = jedis.get("guangzhou-3");
		System.out.println(name.equals("1"));
		System.out.println(jedis.keys("*").size());
	}
}
