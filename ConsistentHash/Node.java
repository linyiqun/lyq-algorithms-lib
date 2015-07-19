package ConsistentHash;

/**
 * 节点类
 * @author lyq
 *
 */
public class Node implements Comparable<Node>{
	//节点名称
	String name;
	//机器的IP地址
	String ip;
	//节点的hash值
	Long hashValue;
	
	public Node(String name, String ip, long hashVaule){
		this.name = name;
		this.ip = ip;
		this.hashValue = hashVaule;
	}

	@Override
	public int compareTo(Node o) {
		// TODO Auto-generated method stub
		return this.hashValue.compareTo(o.hashValue);
	}
}
