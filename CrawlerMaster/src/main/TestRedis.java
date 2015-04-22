package main;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;
import util.RedisUtil;

public class TestRedis {

	public static void main(String[] args){
		try {
			getUnVisistedUrl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test(){
		Jedis jedis = RedisUtil.getInstance();
		String name = jedis.get("guangzhou-3");
		System.out.println(name.equals("1"));
		System.out.println(jedis.keys("*").size());
	}
	
	public static void getUnVisistedUrl() throws Exception{
		Jedis jedis = RedisUtil.getInstance();
		Set<String> keySet = jedis.keys("*");
		Iterator<String> iterator = keySet.iterator();
		PrintWriter writer = new PrintWriter("data/unvisited.txt");
		int count = 0;
		while(iterator.hasNext()){
			String key = iterator.next();
			if(jedis.get(key).equals("0")){
				count ++;
				String surl = key.substring(0, key.lastIndexOf("-"));
				writer.println(surl);
			}
		}
		writer.flush();
		writer.close();
		System.out.println(count);
	}
}
