package LCA;

/**
 * 树结点类
 * @author lyq
 *
 */
public class TreeNode {
	//树结点值
	int value;
	//孩子节点，不一定只有2个节点
	TreeNode[] childNodes;
	
	public TreeNode(){
		
	}
	
	public TreeNode(int value){
		this.value = value;
	}
	
	public TreeNode(String value){
		this.value = Integer.parseInt(value);
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public TreeNode[] getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(TreeNode[] childNodes) {
		this.childNodes = childNodes;
	}
}
