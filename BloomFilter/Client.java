package BloomFilter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * BloomFileter布隆过滤器测试类
 * 
 * @author lyq
 * 
 */
public class Client {
	public static void main(String[] args) {
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\realSample.txt";
		String testFilePath = "C:\\Users\\lyq\\Desktop\\icon\\testInput.txt";
		// 总的查询词数
		int totalCount;
		// 正确的结果数
		int rightCount;
		long startTime = 0;
		long endTime = 0;
		// 布隆过滤器查询结果
		Map<String, Boolean> bfMap;
		// 普通过滤器查询结果
		Map<String, Boolean> nfMap;
		//查询总数据
		ArrayList<String> queryDatas;

		BloomFilterTool tool = new BloomFilterTool(filePath, testFilePath);

		// 采用布隆过滤器的方式进行词的查询
		startTime = System.currentTimeMillis();
		bfMap = tool.queryDatasByBF();
		endTime = System.currentTimeMillis();
		System.out.println("BloomFilter算法耗时" + (endTime - startTime) + "ms");

		// 采用普通过滤器的方式进行词的查询
		startTime = System.currentTimeMillis();
		nfMap = tool.queryDatasByNF();
		endTime = System.currentTimeMillis();
		System.out.println("普通遍历查询操作耗时" + (endTime - startTime) + "ms");

		boolean isExist;
		boolean isExist2;

		rightCount = 0;
		queryDatas = tool.getQueryDatas();
		totalCount = queryDatas.size();
		for (String qWord: queryDatas) {
			// 以遍历的查询的结果作为标准结果
			isExist = nfMap.get(qWord);
			isExist2 = bfMap.get(qWord);

			if (isExist == isExist2) {
				rightCount++;
			}else{
				System.out.println("预判错误的词语：" + qWord);
			}
		}
		System.out.println(MessageFormat.format(
				"Bloom Filter的正确个数为{0}，总查询数为{1}个，正确率{2}", rightCount,
				totalCount, 1.0 * rightCount / totalCount));
	}
}
