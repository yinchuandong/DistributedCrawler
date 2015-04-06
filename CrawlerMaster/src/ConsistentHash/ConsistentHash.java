package ConsistentHash;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;


public class ConsistentHash<T> {

	/**
	 * hash function to calculate hashCode for specific object
	 */
	private final HashFunction hashFunction;
	/**
	 * virtual node
	 */
	private final int numberOfReplicas;
	/**
	 * to restore the specific object corresponding to its hashCode key calculated in MD5
	 */
	private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

	
	public ConsistentHash(HashFunction hashFunction, int numberOfReplicas){
		this(hashFunction, numberOfReplicas, null);
	}
	
	/**
	 * 
	 * @param hashFunction specific hash function to calculate hash code
	 * @param numberOfReplicas the number of virtual nodes
	 * @param nodes the set of real nodes
	 */
	public ConsistentHash(HashFunction hashFunction, int numberOfReplicas,
			Collection<T> nodes) {
		this.hashFunction = hashFunction;
		this.numberOfReplicas = numberOfReplicas;

		if(nodes != null){
			for (T node : nodes) {
				add(node);
			}
		}
	}
	
	/**
	 * add a real node to consistent hash, and it will automatically generate numberOfReplicas virtual nodes
	 * @param node
	 */
	public void add(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.put(hashFunction.hash(node.toString() + i), node);
		}
	}

	/**
	 * remove a real node, and also remove the corresponding virtual nodes
	 * @param node
	 */
	public void remove(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.remove(hashFunction.hash(node.toString() + i));
		}
	}
	
	public void clear(){
		circle.clear();
	}

	/**
	 * get the specific node by a key, <br/>
	 * notice that the calculation of key's hashCode would better to be consistent to that of node <br/>
	 * if the object's key is not in sorted map's keySet, it will cut a subset of sorted map from the object's keyCode<br/>
	 * and return the node whose key is between object's keyCode and the max keyCode in sorted map
	 * @param key
	 * @return
	 */
	public T get(Object key) {
		if (circle.isEmpty()) {
			return null;
		}
		int hash = hashFunction.hash(key);
		if (!circle.containsKey(hash)) {
			SortedMap<Integer, T> tailMap = circle.tailMap(hash);
			hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		}
		return circle.get(hash);
	}

	
}
