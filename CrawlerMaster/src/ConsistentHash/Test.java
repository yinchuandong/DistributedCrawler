package ConsistentHash;

import java.util.Collection;
import java.util.HashSet;

public class Test {
	

	public static void main(String[] args) {
		
		Collection<ServerNode> set = new HashSet<ServerNode>();
		set.add(new ServerNode("192.168.1.1",9001));
		set.add(new ServerNode("192.168.1.2",9002));
		set.add(new ServerNode("192.168.1.3",9003));
		set.add(new ServerNode("192.168.1.4",9004));
		
		ConsistentHash<ServerNode> consistentHash = new ConsistentHash<ServerNode>(new HashFunction(), 1000, set);
		
//		System.out.println(consistentHash.get("192.168.1.0").getIp());
//		System.out.println(consistentHash.get("192.168.1.1").getIp());
//		System.out.println(consistentHash.get("192.168.1.2").getIp());
//		System.out.println(consistentHash.get("192.168.1.3").getIp());
//		System.out.println(consistentHash.get("192.168.1.4").getIp());
//		System.out.println(consistentHash.get("192.168.1.5").getIp());
//		System.out.println(consistentHash.get("192.168.1.9").getIp());
		for (int i = 0; i < 50; i++) {
			System.out.println(consistentHash.get("192.168.1." + i).getIp());
		}
//		consistentHash.remove(new ServerNode("192.168.1.1",9001));
//		System.out.println("------------------------------");
//		for (int i = 0; i < 20; i++) {
//			System.out.println(consistentHash.get("192.168.1." + i).getIp());
//		}

	}

}
