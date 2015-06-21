package TopK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计工具类
 * 
 * @author lyq
 * 
 */
public class StatisticTool {
	// 哈希表存放查询词以及查询数
	public static int[] countMap;

	// query查询文件地址
	private String filePath;
	// 哈希表容量
	private int mapCotainNum;
	// 查询词集
	private ArrayList<String> queryWords;
	// 存放查询词计数键值对
	private Map<String, Integer> query2Count;

	public StatisticTool(String filePath, int mapCotainNum) {
		this.filePath = filePath;
		this.mapCotainNum = mapCotainNum;
		
		//执行初始化操作
		initOperation();
		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 */
	private void readDataFile() {
		File file = new File(filePath);
		ArrayList<String> dataArray = new ArrayList<String>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] array;
			
			while ((str = in.readLine()) != null) {
				array = str.split(" ");
				
				for(String s: array){
					dataArray.add(s);
				}
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		queryWords = dataArray;
	}

	/**
	 * 初始化操作，在每次进行统计操作前进行
	 */
	public void initOperation() {
		this.countMap = new int[mapCotainNum];
		this.query2Count = new HashMap<String, Integer>();
	}

	/**
	 * 对总查询词进行冒泡排序操作
	 */
	public String[] sortQuerys() {
		int k;
		String str1;
		String str2;
		String temp;
		String[] tempWords;

		tempWords = new String[queryWords.size()];
		queryWords.toArray(tempWords);

		// 通过冒泡排序对查询词进行排序
		for (int i = 0; i < tempWords.length - 1; i++) {
			k = i;

			for (int j = i + 1; j < tempWords.length; j++) {
				str1 = tempWords[k];
				str2 = tempWords[j];

				if (str1.compareTo(str2) > 0) {
					k = j;
				}
			}

			if (k != i) {
				temp = tempWords[i];
				tempWords[i] = tempWords[k];
				tempWords[k] = temp;
			}
		}

		return tempWords;
	}

	/**
	 * 通过外部排序的算法实现统计
	 */
	public void statisticBySort() {
		int count;
		//最后的词是否相等
		boolean isEndSame;
		//上一个词
		String lastWord;
		String[] sortedWord;

		sortedWord = sortQuerys();

		lastWord = sortedWord[0];
		count = 0;
		isEndSame = false;
		
		this.query2Count.clear();
		// 进行线性扫描统计
		for (String w : sortedWord) {
			// 如果本次的词等于上次的词，则计数加1
			if (w.equals(lastWord)) {
				count++;
				isEndSame = true;
			} else {
				// 将上次的词存入map
				query2Count.put(lastWord, count);
				
				//重置操作
				lastWord = w;
				count = 1;
				isEndSame = false;
			}
		}
		
		//如果最后的词是相等的，则，统计解法存入
		if(isEndSame){
			query2Count.put(lastWord, count);
		}
	}

	/**
	 * 用哈希表的方法进行查询词的统计计数
	 */
	public void statisticByHash() {
		long pos;
		int count;

		count = 0;
		pos = -1;
		
		this.query2Count.clear();
		for (String word : queryWords) {
			pos = HashTool.BKDRHash(word);
			pos %= mapCotainNum;

			if (countMap[(int) pos] != 0) {
				countMap[(int) pos]++;
			} else {
				//countMap中的数组默认值为0
				countMap[(int) pos] = 1;
			}
		}

		// 将统计结果存入map中，供下个阶段使用
		for (String word : queryWords) {
			pos = HashTool.BKDRHash(word);
			pos %= mapCotainNum;

			count = countMap[(int) pos];
			// 直接存入map中
			query2Count.put(word, count);
		}
	}

	/**
	 * 获取计数图
	 * @return
	 */
	public Map<String, Integer> getQuery2Count() {
		return this.query2Count;
	}

}
