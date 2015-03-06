package LCA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LCA最近公共祖先算法
 * 
 * @author lyq
 * 
 */
public class LCATool {
	// 节点数据文件
	private String dataFilePath;
	// 查询请求数据文件
	private String queryFilePath;
	// 节点祖先集合,数组下标代表所对应的节点，数组组为其祖先值
	private int[] ancestor;
	// 标记数组，代表此节点是否已经被访问过
	private boolean[] checked;
	// 请求数据组
	private ArrayList<int[]> querys;
	// 请求结果值
	private int[][] resultValues;
	// 初始数据值
	private ArrayList<String> totalDatas;

	public LCATool(String dataFilePath, String queryFilePath) {
		this.dataFilePath = dataFilePath;
		this.queryFilePath = queryFilePath;

		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 */
	private void readDataFile() {
		File file = new File(dataFilePath);
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

		totalDatas = new ArrayList<>();
		for (String[] array : dataArray) {
			for (String s : array) {
				totalDatas.add(s);
			}
		}
		checked = new boolean[totalDatas.size() + 1];
		ancestor = new int[totalDatas.size() + 1];

		// 读取查询请求数据
		file = new File(queryFilePath);
		dataArray.clear();
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

		int x = 0;
		int y = 0;
		querys = new ArrayList<>();
		resultValues = new int[dataArray.size()][dataArray.size()];

		for (int i = 0; i < dataArray.size(); i++) {
			for (int j = 0; j < dataArray.size(); j++) {
				// 值-1代表还未计算过LCA值
				resultValues[i][j] = -1;
			}
		}

		for (String[] array : dataArray) {
			x = Integer.parseInt(array[0]);
			y = Integer.parseInt(array[1]);

			querys.add(new int[] { x, y });
		}

	}

	/**
	 * 构建树结构，此处默认构造成二叉树的形式，真实情况根据实际问题需要
	 * 
	 * @param rootNode
	 *            根节点参数
	 */
	private void createTree(TreeNode rootNode) {
		TreeNode tempNode;
		TreeNode[] nodeArray;
		ArrayList<String> dataCopy;
		LinkedBlockingQueue<TreeNode> nodeSeqs = new LinkedBlockingQueue<>();

		rootNode.setValue(Integer.parseInt(totalDatas.get(0)));
		dataCopy = (ArrayList<String>) totalDatas.clone();
		// 移除根节点的首个数据值
		dataCopy.remove(0);
		nodeSeqs.add(rootNode);

		while (!nodeSeqs.isEmpty()) {
			tempNode = nodeSeqs.poll();

			nodeArray = new TreeNode[2];
			if (dataCopy.size() > 0) {
				nodeArray[0] = new TreeNode(dataCopy.get(0));
				dataCopy.remove(0);
				nodeSeqs.add(nodeArray[0]);
			} else {
				tempNode.setChildNodes(nodeArray);
				break;
			}

			if (dataCopy.size() > 0) {
				nodeArray[1] = new TreeNode(dataCopy.get(0));
				dataCopy.remove(0);
				nodeSeqs.add(nodeArray[1]);
			} else {
				tempNode.setChildNodes(nodeArray);
				break;
			}

			tempNode.setChildNodes(nodeArray);
		}
	}

	/**
	 * 进行lca最近公共祖先算法的计算
	 * 
	 * @param node
	 *            当前处理的节点
	 */
	private void lcaCal(TreeNode node) {
		if (node == null) {
			return;
		}

		// 处理过后的待删除请求列表
		ArrayList<int[]> deleteQuerys = new ArrayList<>();
		TreeNode[] childNodes;
		int value = node.value;
		ancestor[value] = value;

		childNodes = node.getChildNodes();
		if (childNodes != null) {
			for (TreeNode n : childNodes) {
				lcaCal(n);

				// 深度优先遍历完成，重新设置祖先值
				value = node.value;
				//通过树型结构进行祖先的设置方式，易于理解
				// setNodeAncestor(n, value);
				if(n != null){
					//合并2个集合
					unionSet(n.value, value);
				}
			}
		}

		// 标记此点被访问过
		checked[node.value] = true;
		int[] queryArray;
		for (int i = 0; i < querys.size(); i++) {
			queryArray = querys.get(i);

			if (queryArray[0] == node.value) {
				// 如果此时另一点已经被访问过
				if (checked[queryArray[1]]) {
					resultValues[queryArray[0]][queryArray[1]] = findSet(queryArray[1]);

					System.out.println(MessageFormat.format(
							"节点{0}和{1}的最近公共祖先为{2}", queryArray[0],
							queryArray[1],
							resultValues[queryArray[0]][queryArray[1]]));

					deleteQuerys.add(querys.get(i));
				}
			} else if (queryArray[1] == node.value) {
				// 如果此时另一点已经被访问过
				if (checked[queryArray[0]]) {
					resultValues[queryArray[0]][queryArray[1]] = findSet(queryArray[0]);

					System.out.println(MessageFormat.format(
							"节点{0}和{1}的最近公共祖先为{2}", queryArray[0],
							queryArray[1],
							resultValues[queryArray[0]][queryArray[1]]));
					deleteQuerys.add(querys.get(i));
				}
			}
		}

		querys.removeAll(deleteQuerys);
	}

	/**
	 * 寻找节点x属于哪个集合，就是寻找x的最早的祖先
	 * 
	 * @param x
	 */
	private int findSet(int x) {
		// 如果祖先不是自己，则继续往父亲节点寻找
		if (x != ancestor[x]) {
			ancestor[x] = findSet(ancestor[x]);
		}

		return ancestor[x];
	}

	/**
	 * 将集合x所属集合合并到y集合中
	 * 
	 * @param x
	 * @param y
	 */
	public void unionSet(int x, int y) {
		// 找到x和y节点的祖先
		int ax = findSet(x);
		int ay = findSet(y);

		// 如果2个祖先是同一个，则表示是同一点，直接返回
		if (ax != ay) {
			// ax的父亲指向y节点的祖先ay
			ancestor[ax] = ay;
		}
	}

	/**
	 * 设置节点的祖先值
	 * 
	 * @param node
	 *            待设置节点
	 * @param value
	 *            目标值
	 */
	private void setNodeAncestor(TreeNode node, int value) {
		if (node == null) {
			return;
		}

		TreeNode[] childNodes;
		ancestor[node.value] = value;

		// 递归设置节点的子节点的祖先值
		childNodes = node.childNodes;
		if (childNodes != null) {
			for (TreeNode n : node.childNodes) {
				setNodeAncestor(n, value);
			}
		}

	}

	/**
	 * 执行离线查询
	 */
	public void executeOfflineQuery() {
		TreeNode rootNode = new TreeNode();

		createTree(rootNode);
		lcaCal(rootNode);

		System.out.println("查询请求数剩余总数" + querys.size() + "条");
	}
}
