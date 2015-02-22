package ConsistentHash;

public class HashFunction {
	int hash(Object key) {
		// md5加密后，hashcode
		return MD5.md5(key.toString()).hashCode();
	}
}
