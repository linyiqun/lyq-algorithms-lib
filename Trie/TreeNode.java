package Trie;

import java.util.ArrayList;

/**
 * 
 * 
 * 
 * @author lyq
 * 
 * 
 */
public class TreeNode {
	//节点的值
	String value;
	//节点孩子节点
	ArrayList<TreeNode> childNodes;

	public TreeNode(String value) {
		this.value = value;
		this.childNodes = new ArrayList<TreeNode>();
	}

	public ArrayList<TreeNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(ArrayList<TreeNode> childNodes) {
		this.childNodes = childNodes;
	}
}
