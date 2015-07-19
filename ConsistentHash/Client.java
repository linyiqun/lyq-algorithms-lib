package ConsistentHash;

import java.util.ArrayList;

/**
 * 一致性哈希算法测试类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//测试数据
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input.txt";
		int virtualNodeNum;
		ArrayList<Entity> testEntities;
		ConsistentHashTool tool;
		
		virtualNodeNum = 3;
		testEntities = new ArrayList<>();
		testEntities.add(new Entity("ZhangSan", 100));
		testEntities.add(new Entity("LiSi", 200));
		testEntities.add(new Entity("WangWu", 300));
		
		tool = new ConsistentHashTool(filePath, virtualNodeNum, testEntities);
		System.out.println("采用一致性哈希实现分配");
		tool.hashAssigned();
		System.out.println("\n采用虚拟节点一致性哈希实现分配");
		tool.hashAssignedByVirtualNode();
	}
}
