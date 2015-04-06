package BloomFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 布隆过滤器算法工具类
 * 
 * @author lyq
 * 
 */
public class BloomFilterTool {
	// 位数组设置为10w位的长度
	public static final int BIT_ARRAY_LENGTH = 100000;

	// 原始文档地址
	private String filePath;
	// 测试文档地址
	private String testFilePath;
	// 用于存储的位数组,一个单元用1个位存储
	private BitSet bitStore;
	// 原始数据
	private ArrayList<String> totalDatas;
	// 测试的查询数据
	private ArrayList<String> queryDatas;

	public BloomFilterTool(String filePath, String testFilePath) {
		this.filePath = filePath;
		this.testFilePath = testFilePath;

		this.totalDatas = readDataFile(this.filePath);
		this.queryDatas = readDataFile(this.testFilePath);
	}

	/**
	 * 从文件中读取数据
	 */
	public ArrayList<String> readDataFile(String path) {
		File file = new File(path);
		ArrayList<String> dataArray = new ArrayList<String>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				for(String word: tempArray){
					dataArray.add(word);
				}
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		return dataArray;
	}
	
	/**
	 * 获取查询总数据
	 * @return
	 */
	public ArrayList<String> getQueryDatas(){
		return this.queryDatas;
	}

	/**
	 * 用位存储数据
	 */
	private void bitStoreData() {
		long hashcode = 0;
		bitStore = new BitSet(BIT_ARRAY_LENGTH);

		for (String word : totalDatas) {
			// 对每个词进行3次哈希求值，减少哈希冲突的概率
			hashcode = BKDRHash(word);
			hashcode %= BIT_ARRAY_LENGTH;

			
			bitStore.set((int) hashcode, true);

			hashcode = SDBMHash(word);
			hashcode %= BIT_ARRAY_LENGTH;

			bitStore.set((int) hashcode, true);

			hashcode = DJBHash(word);
			hashcode %= BIT_ARRAY_LENGTH;

			bitStore.set((int) hashcode, true);
		}
	}

	/**
	 * 进行数据的查询，判断原数据中是否存在目标查询数据
	 */
	public Map<String, Boolean> queryDatasByBF() {
		boolean isExist;
		long hashcode;
		int pos1;
		int pos2;
		int pos3;
		// 查询词的所属情况图
		Map<String, Boolean> word2exist = new HashMap<String, Boolean>();

		hashcode = 0;
		isExist = false;
		bitStoreData();
		for (String word : queryDatas) {
			isExist = false;
			
			hashcode = BKDRHash(word);
			pos1 = (int) (hashcode % BIT_ARRAY_LENGTH);

			hashcode = SDBMHash(word);
			pos2 = (int) (hashcode % BIT_ARRAY_LENGTH);

			hashcode = DJBHash(word);
			pos3 = (int) (hashcode % BIT_ARRAY_LENGTH);

			// 只有在3个哈希位置都存在才算真的存在
			if (bitStore.get(pos1) && bitStore.get(pos2) && bitStore.get(pos3)) {
				isExist = true;
			}

			// 将结果存入map
			word2exist.put(word, isExist);
		}

		return word2exist;
	}

	/**
	 * 进行数据的查询采用普通的过滤器方式就是，逐个查询
	 */
	public Map<String, Boolean> queryDatasByNF() {
		boolean isExist = false;
		// 查询词的所属情况图
		Map<String, Boolean> word2exist = new HashMap<String, Boolean>();

		// 遍历的方式去查找
		for (String qWord : queryDatas) {
			isExist = false;
			for (String word : totalDatas) {
				if (qWord.equals(word)) {
					isExist = true;
					break;
				}
			}

			word2exist.put(qWord, isExist);
		}

		return word2exist;
	}

	/**
	 * BKDR字符哈希算法
	 * 
	 * @param str
	 * @return
	 */
	private long BKDRHash(String str) {
		int seed = 31; /* 31 131 1313 13131 131313 etc.. */
		long hash = 0;
		int i = 0;

		for (i = 0; i < str.length(); i++) {
			hash = (hash * seed) + (str.charAt(i));
		}

		hash = Math.abs(hash);
		return hash;
	}

	/**
	 * SDB字符哈希算法
	 * 
	 * @param str
	 * @return
	 */
	private long SDBMHash(String str) {
		long hash = 0;
		int i = 0;
		
		for (i = 0; i < str.length(); i++) {
			hash = (str.charAt(i)) + (hash << 6) + (hash << 16) - hash;
		}

		hash = Math.abs(hash);
		return hash;
	}

	/**
	 * DJB字符哈希算法
	 * 
	 * @param str
	 * @return
	 */
	private long DJBHash(String str) {
		long hash = 5381;
		int i = 0;

		for (i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + (str.charAt(i));
		}

		hash = Math.abs(hash);
		return hash;
	}

}
