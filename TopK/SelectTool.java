package TopK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * 筛选出TopK的算法工具类
 * 
 * @author lyq
 * 
 */
public class SelectTool {
	// 筛选的前K个值的K数值
	private int k;
	// 计数统计图
	private Map<String, Integer> countMap;
	// 筛选出的TopK的查询数据
	private ArrayList<Query> queryList;

	public SelectTool(int k, Map<String, Integer> countMap) {
		this.k = k;
		this.countMap = countMap;
	}

	/**
	 * 利用外部排序进行TopK的选举,维护K个变量
	 */
	public void selectTopKBySort() {
		int index;
		int count;
		String queryWord;
		Query insertQuery;
		Query query;
		Query query2;

		index = 0;
		queryList = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
			index++;
			count = entry.getValue();
			queryWord = entry.getKey();
			insertQuery = new Query(count, queryWord);

			if (index < k) {
				queryList.add(insertQuery);
			} else if (index == k) {
				queryList.add(insertQuery);
				// 对查询结果进行初次排序
				Collections.sort(queryList);
			} else if (index > k) {
				for (int i = 0; i < queryList.size() - 1; i++) {
					query = queryList.get(i);
					query2 = queryList.get(i + 1);

					// 寻找插入的位置，如果count值在前后query之间，则进行替换
					if (query.count >= insertQuery.count
							&& query2.count < insertQuery.count) {
						queryList.set(i + 1, insertQuery);
						break;
					}
				}
			}
		}
		
		outputTopKQuerys();
	}

	/**
	 * 通过堆排序算法进行TopK的筛选
	 */
	public void selectTopKByMaxHeap() {
		int index;
		int count;
		String queryWord;
		Query insertQuery;

		index = 0;
		queryList = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
			index++;
			count = entry.getValue();
			queryWord = entry.getKey();
			insertQuery = new Query(count, queryWord);

			if (index < k) {
				queryList.add(insertQuery);
			} else if (index == k) {
				queryList.add(insertQuery);

				// 如果刚刚填满k个查询量，则进行初始堆排序
				queryList = initMaxHeap(queryList);
			} else if (index > k) {
				// 插入一个新的查询值，并维护这个堆结构
				adjustHeap(insertQuery, queryList);
			}
		}
		
		outputTopKQuerys();
	}

	/**
	 * 初始化个数为k的大顶堆
	 * 
	 * @param queryList
	 *            返回排好序的新的堆
	 * @return
	 */
	private ArrayList<Query> initMaxHeap(ArrayList<Query> queryList) {
		// 第一个查询词
		Query firstQuery;
		ArrayList<Query> newMaxHeap;

		newMaxHeap = new ArrayList<>();
		for (int i = 0; i < k; i++) {
			adjustMinValueFromHeap(queryList);

			// 将第一个元素与最后一个元素互换
			firstQuery = queryList.get(0);

			newMaxHeap.add(firstQuery);
			// 将第一个用无限小替代
			queryList.set(0, new Query(-Integer.MAX_VALUE, null));
		}

		return newMaxHeap;
	}

	/**
	 * 选出当前堆中最小的元素，与最后一个位置的元素进行交换
	 * 
	 * @param queryList
	 *            目前维护的大顶堆
	 */
	private void adjustMinValueFromHeap(ArrayList<Query> queryList) {
		int currentIndex;
		int otherIndex;
		int leafIndex;
		Query temp;
		Query query;
		Query query2;
		Query parentQuery;

		// 计算叶子节点的最小下标号
		leafIndex = k / 2;

		for (int i = leafIndex; i < k; i += 2) {
			currentIndex = i;

			// 如果当前判断还没有到根节点
			while (currentIndex > 0) {
				query = queryList.get(currentIndex);

				// 判断节点是否为左子节点还是右子节点，再判断取哪侧的节点
				if (currentIndex % 2 == 0) {
					otherIndex = currentIndex - 1;
					query2 = queryList.get(otherIndex);
				} else {
					otherIndex = currentIndex + 1;
					query2 = queryList.get(otherIndex);
				}

				// 赋值子节点下标
				if (query.count < query2.count) {
					currentIndex = otherIndex;
					temp = query2;
				} else {
					temp = query;
				}
				parentQuery = queryList.get((currentIndex - 1) / 2);

				// 重新进行赋值操作
				if (temp.count > parentQuery.count) {
					queryList.set((currentIndex - 1) / 2, temp);
					queryList.set(currentIndex, parentQuery);
				}

				// 比较操作向上回溯
				currentIndex = (currentIndex - 1) / 2;
			}
		}
	}

	/**
	 * 进行大顶堆的调整
	 * 
	 * @param insertQuery
	 *            待插入的查询词
	 * @param queryList
	 *            堆数据
	 */
	public void adjustHeap(Query insertQuery, ArrayList<Query> queryList) {
		int currentIndex;
		int leftIndex;
		int rightIndex;

		Query query;
		Query leftQuery;
		Query rightQuery;

		currentIndex = 0;
		while (currentIndex < queryList.size()) {
			query = queryList.get(currentIndex);

			// 如果待插入的查询计数比当前大，则做替换
			if (insertQuery.count > query.count) {
				queryList.set(currentIndex, insertQuery);
				break;
			} else {
				leftIndex = 2 * (currentIndex + 1) - 1;
				rightIndex = 2 * (currentIndex + 1);

				leftQuery = queryList.get(leftIndex);
				rightQuery = queryList.get(rightIndex);

				// 选择一个计数值较小的做递归比较
				if (leftQuery.count < rightQuery.count) {
					// 下标做变换
					currentIndex = leftIndex;
					query = leftQuery;
				} else {
					// 下标做变换
					currentIndex = rightIndex;
					query = rightQuery;
				}
			}
		}
	}

	/**
	 * 输出TopK的统计结果
	 */
	private void outputTopKQuerys() {
		int i = 0;

		for (Query q : queryList) {
			System.out.println("Top " + (i+1) + ":" + q.word + ":计数" + q.count);
			i++;
		}
	}
}
