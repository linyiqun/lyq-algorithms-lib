package Trie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * 
 * 
 * @author lyq
 * 
 * 
 */
public class TrieTool {
	// 测试数据文件地址
	private String filePath;
	// 原始数据
	private ArrayList<String[]> datas;

	public TrieTool(String filePath) {
		this.filePath = filePath;
		readDataFile();
	}

	/**
	 * 
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
				tempArray = new String[str.length()];
				for (int i = 0; i < str.length(); i++) {
					tempArray[i] = str.charAt(i) + "";
				}
				dataArray.add(tempArray);
			}

			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		datas = dataArray;
	}

	/**
	 * 
	 * 构造Trie树
	 * 
	 * 
	 * 
	 * @return
	 */
	public TreeNode constructTrieTree() {
		TreeNode rootNode = new TreeNode(null);
		ArrayList<String> tempStr;

		for (String[] array : datas) {
			tempStr = new ArrayList<String>();

			for (String s : array) {
				tempStr.add(s);
			}

			// 逐个字符串的添加
			addStrToTree(rootNode, tempStr);
		}

		return rootNode;
	}

	/**
	 * 
	 * 添加字符串的内容到Trie树中
	 * 
	 * 
	 * 
	 * @param node
	 * 
	 * @param strArray
	 */
	private void addStrToTree(TreeNode node, ArrayList<String> strArray) {
		boolean hasValue = false;
		TreeNode tempNode;
		TreeNode currentNode = null;

		// 子节点中遍历寻找与当前第一个字符对应的节点
		for (TreeNode childNode : node.childNodes) {
			if (childNode.value.equals(strArray.get(0))) {
				hasValue = true;
				currentNode = childNode;
				break;
			}

		}

		// 如果没有找到对应节点，则将此节点作为新的节点
		if (!hasValue) {
			// 遍历到了未曾存在的字符值的，则新键节点作为当前节点的子节点
			tempNode = new TreeNode(strArray.get(0));
			// node.childNodes.add(tempNode);
			insertNode(node.childNodes, tempNode);
			currentNode = tempNode;
		}
		strArray.remove(0);

		// 如果字符已经全部查找完毕，则跳出循环
		if (strArray.size() == 0) {
			return;
		} else {
			addStrToTree(currentNode, strArray);
		}
	}

	/**
	 * 
	 * 将新建的节点按照字母排序的顺序插入到孩子节点中
	 * 
	 * 
	 * 
	 * @param childNodes
	 * 
	 *            孩子节点
	 * 
	 * @param node
	 * 
	 *            新键的待插入的节点
	 */
	private void insertNode(ArrayList<TreeNode> childNodes, TreeNode node) {
		String value = node.value;
		int insertIndex = 0;

		for (int i = 0; i < childNodes.size() - 1; i++) {
			if (childNodes.get(i).value.compareTo(value) <= 0
					&& childNodes.get(i + 1).value.compareTo(value) > 0) {
				insertIndex = i + 1;
				break;
			}
		}

		if (childNodes.size() == 0) {
			childNodes.add(node);
		} else if (childNodes.size() == 1) {
			// 只有1个的情况额外判断
			if (childNodes.get(0).value.compareTo(value) > 0) {
				childNodes.add(0, node);
			} else {
				childNodes.add(node);
			}
		} else {
			childNodes.add(insertIndex, node);
		}

	}

}
