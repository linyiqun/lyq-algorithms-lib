package TopK;

import java.util.Map;

/**
 * TopK场景测试类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//测试数据文件路径
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input.txt";
		int mapCotainNum;
		int k;
		StatisticTool statTool;
		SelectTool selectTool;
		Map<String, Integer> countMap;
		
		//哈希表容器大小1W个
		mapCotainNum = 10000;
		k = 7;
		statTool = new StatisticTool(filePath, mapCotainNum);
		
		statTool.statisticBySort();
		statTool.statisticByHash();
		countMap = statTool.getQuery2Count();
		
		selectTool = new SelectTool(k, countMap);
		System.out.println("普通排序算法实现TopK");
		selectTool.selectTopKBySort();
		System.out.println();
		
		System.out.println("堆排序算法实现TopK");
		selectTool.selectTopKByMaxHeap();
	}
}
