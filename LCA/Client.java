package LCA;

/**
 * LCA最近公共祖先算法测试类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//节点数据文件
		String dataFilePath = "C:\\Users\\lyq\\Desktop\\icon\\dataFile.txt";
		//查询请求数据文件
		String queryFilePath = "C:\\Users\\lyq\\Desktop\\icon\\queryFile.txt";
		
		LCATool tool = new LCATool(dataFilePath, queryFilePath);
		tool.executeOfflineQuery();
	}
}
