package ConsistentHash;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 一致性哈希算法工具类
 * 
 * @author lyq
 * 
 */
public class ConsistentHashTool {
	// 机器节点信息文件地址
	private String filePath;
	// 每个节点虚拟节点的个数
	private int virtualNodeNum;
	// 测试实体对象列表
	private ArrayList<Entity> entityLists;
	// 节点列表
	private ArrayList<Node> totalNodes;
	// 结果分配列表
	private HashMap<Entity, Node> assignedResult;

	public ConsistentHashTool(String filePath, int virtualNodeNum,
			ArrayList<Entity> entityLists) {
		this.filePath = filePath;
		this.virtualNodeNum = virtualNodeNum;
		this.entityLists = entityLists;

		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 */
	private void readDataFile() {
		File file = new File(filePath);
		ArrayList<String[]> dataArray = new ArrayList<String[]>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		Node node;
		String name;
		String ip;
		long hashValue;

		this.totalNodes = new ArrayList<>();
		// 解析出每行的节点名称和ip地址
		for (String[] array : dataArray) {
			name = array[0];
			ip = array[1];

			// 根据IP地址进行hash映射
			hashValue = ip.hashCode();
			node = new Node(name, ip, hashValue);
			this.totalNodes.add(node);
		}

		// 对节点按照hashValue值进行升序排列
		Collections.sort(this.totalNodes);
	}

	/**
	 * 哈希算法分配对象实例
	 */
	public void hashAssigned() {
		Node desNode;

		this.assignedResult = new HashMap<>();
		for (Entity e : this.entityLists) {
			desNode = selectDesNode(e, this.totalNodes);

			this.assignedResult.put(e, desNode);
		}

		outPutAssginedResult();
	}

	/**
	 * 通过虚拟节点的哈希算法分配
	 */
	public void hashAssignedByVirtualNode() {
		String name;
		String ip;
		long hashValue;

		// 用以生成随机数数字后缀
		Random random;
		Node node;
		ArrayList<Node> virtualNodes;

		random = new Random();
		// 创建虚拟节点
		virtualNodes = new ArrayList<>();
		for (Node n : this.totalNodes) {
			name = n.name;
			ip = n.ip;

			// 复制虚拟节点个数
			for (int i = 0; i < this.virtualNodeNum; i++) {
				// 虚拟节点的哈希值用ip+数字后缀的形式生成
				hashValue = (ip + "#" + (random.nextInt(1000) + 1)).hashCode();

				node = new Node(name, ip, hashValue);
				virtualNodes.add(node);
			}
		}
		// 进行升序排序
		Collections.sort(virtualNodes);

		// 哈希算法分配节点
		Node desNode;
		this.assignedResult = new HashMap<>();
		for (Entity e : this.entityLists) {
			desNode = selectDesNode(e, virtualNodes);

			this.assignedResult.put(e, desNode);
		}

		outPutAssginedResult();
	}

	/**
	 * 在哈希环中寻找归属的节点
	 * 
	 * @param entity
	 *            待分配的实体
	 * @param nodeList
	 *            节点列表
	 * @return
	 */
	private Node selectDesNode(Entity entity, ArrayList<Node> nodeList) {
		Node desNode;
		int hashValue;

		desNode = null;
		hashValue = entity.hashCode();

		for (Node n : nodeList) {
			// 按照顺时针方向，选择一个距离最近的哈希值节点
			if (n.hashValue > hashValue) {
				desNode = n;
				break;
			}
		}

		// 如果没有找到说明已经超过最大的hashValue,按照环状，被划分到第一个
		if (desNode == null) {
			desNode = nodeList.get(0);
		}

		return desNode;
	}

	/**
	 * 输出分配结果
	 */
	private void outPutAssginedResult() {
		Entity e;
		Node n;

		for (Map.Entry<Entity, Node> entry : this.assignedResult.entrySet()) {
			e = entry.getKey();
			n = entry.getValue();

			System.out.println(MessageFormat.format("实体{0}被分配到了节点({1}, {2})",
					e.name, n.name, n.ip));
		}
	}
}
