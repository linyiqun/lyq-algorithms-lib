package Tarjan;

import java.util.ArrayList;

/**
 * 有向图类
 * 
 * @author lyq
 * 
 */
public class Graph {
	// 图包含的点的标号
	ArrayList<Integer> vertices;
	// 图包含的有向边的分布，edges[i][j]中，i，j代表的是图的标号
	int[][] edges;
	// 图数据
	ArrayList<String[]> graphDatas;

	public Graph(ArrayList<String[]> graphDatas) {
		this.graphDatas = graphDatas;
		vertices = new ArrayList<>();
	}

	/**
	 * 利用图数据构造有向图
	 */
	public void constructGraph() {
		int v1 = 0;
		int v2 = 0;
		int verticNum = 0;

		for (String[] array : graphDatas) {
			v1 = Integer.parseInt(array[0]);
			v2 = Integer.parseInt(array[1]);

			if (!vertices.contains(v1)) {
				vertices.add(v1);
			}

			if (!vertices.contains(v2)) {
				vertices.add(v2);
			}
		}

		verticNum = vertices.size();
		// 多申请1个空间，是标号和下标一致
		edges = new int[verticNum + 1][verticNum + 1];

		// 做边的初始化操作，-1 代表的是此方向没有连通的边
		for (int i = 1; i < verticNum + 1; i++) {
			for (int j = 1; j < verticNum + 1; j++) {
				edges[i][j] = -1;
			}
		}

		for (String[] array : graphDatas) {
			v1 = Integer.parseInt(array[0]);
			v2 = Integer.parseInt(array[1]);

			edges[v1][v2] = 1;
		}
	}
}
