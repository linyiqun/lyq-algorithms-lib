package Tarjan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Tarjan算法-有向图强连通分量算法
 * 
 * @author lyq
 * 
 */
public class TarjanTool {
	// 当前节点的遍历号
	public static int currentSeq = 1;

	// 图构造数据文件地址
	private String graphFile;
	// 节点u搜索的次序编号
	private int DFN[];
	// u或u的子树能回溯到的最早的节点的次序编号
	private int LOW[];
	// 由图数据构造的有向图
	private Graph graph;
	// 图遍历节点栈
	private Stack<Integer> verticStack;
	// 强连通分量结果
	private ArrayList<ArrayList<Integer>> resultGraph;
	// 图的未遍历的点的标号列表
	private ArrayList<Integer> remainVertices;
	// 图未遍历的边的列表
	private ArrayList<int[]> remainEdges;

	public TarjanTool(String graphFile) {
		this.graphFile = graphFile;
		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 * 
	 */
	private void readDataFile() {
		File file = new File(graphFile);
		ArrayList<String[]> dataArray = new ArrayList<String[]>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		// 根据数据构造有向图
		graph = new Graph(dataArray);
		graph.constructGraph();
	}
	
	/**
	 * 初始化2个标量数组
	 */
	private void initDfnAndLow(){
		int verticNum = 0;
		verticStack = new Stack<>();
		remainVertices = (ArrayList<Integer>) graph.vertices.clone();
		remainEdges = new ArrayList<>();
		resultGraph = new ArrayList<>();

		for (int i = 0; i < graph.edges.length; i++) {
			remainEdges.add(graph.edges[i]);
		}

		verticNum = graph.vertices.size();
		DFN = new int[verticNum + 1];
		LOW = new int[verticNum + 1];

		// 初始化数组操作
		for (int i = 1; i <= verticNum; i++) {
			DFN[i] = Integer.MAX_VALUE;
			LOW[i] = -1;
		}
	}

	/**
	 * 搜索强连通分量
	 */
	public void searchStrongConnectedGraph() {
		int label = 0;
		int verticNum = graph.vertices.size();
		initDfnAndLow();
		
		// 设置第一个的DFN[1]=1;
		DFN[1] = 1;
		// 移除首个节点
		label = remainVertices.get(0);

		verticStack.add(label);
		remainVertices.remove((Integer) 1);
		while (remainVertices.size() > 0) {
			for (int i = 1; i <= verticNum; i++) {
				if (graph.edges[label][i] == 1) {
					// 把与此边相连的节点也加入栈中
					verticStack.add(i);
					remainVertices.remove((Integer) i);

					dfsSearch(verticStack);
				}
			}

			LOW[label] = searchEarliestDFN(label);
			// 重新回溯到第一个点进行DFN和LOW值的判断
			if (LOW[label] == DFN[label]) {
				popStackGraph(label);
			}
		}

		printSCG();
	}

	/**
	 * 深度优先遍历的方式寻找强连通分量
	 * 
	 * @param stack
	 *            存放的节点的当前栈
	 * @param seqNum
	 *            当前遍历的次序号
	 */
	private void dfsSearch(Stack<Integer> stack) {
		int currentLabel = stack.peek();
		// 设置搜索次序号，在原先的基础上增加1
		currentSeq++;
		DFN[currentLabel] = currentSeq;
		LOW[currentLabel] = searchEarliestDFN(currentLabel);

		int[] edgeVertic;
		edgeVertic = remainEdges.get(currentLabel);
		for (int i = 1; i < edgeVertic.length; i++) {
			if (edgeVertic[i] == 1) {
				// 如果剩余可选节点中包含此节点吗，则此节点添加
				if (remainVertices.contains(i)) {
					stack.add(i);
				} else {
					// 不包含，则跳过
					continue;
				}

				// 将与此边相连的点加入栈中
				remainVertices.remove((Integer) i);
				remainEdges.set(currentLabel, null);

				// 继续深度优先遍历
				dfsSearch(stack);
			}
		}

		if (LOW[currentLabel] == DFN[currentLabel]) {
			popStackGraph(currentLabel);
		}

	}

	/**
	 * 从栈中弹出局部结果
	 * 
	 * @param label
	 *            弹出的临界标号
	 */
	private void popStackGraph(int label) {
		// 如果2个值相等，则将此节点以及此节点后的点移出栈中
		int value = 0;

		ArrayList<Integer> scg = new ArrayList<>();
		while (label != verticStack.peek()) {
			value = verticStack.pop();
			scg.add(0, value);
		}
		scg.add(0, verticStack.pop());

		resultGraph.add(scg);
	}

	/**
	 * 当前的节点可能搜索到的最早的次序号
	 * 
	 * @param label
	 *            当前的节点标号
	 * @return
	 */
	private int searchEarliestDFN(int label) {
		// 判断此节点是否有子边
		boolean hasSubEdge = false;
		int minDFN = DFN[label];

		// 如果搜索到的次序号已经是最小的次序号，则返回
		if (DFN[label] == 1) {
			return DFN[label];
		}

		int tempDFN = 0;
		for (int i = 1; i <= graph.vertices.size(); i++) {
			if (graph.edges[label][i] == 1) {
				hasSubEdge = true;

				// 如果在堆栈中和剩余节点中都未包含此节点说明已经被退栈了，不允许再次遍历
				if (!remainVertices.contains(i) && !verticStack.contains(i)) {
					continue;
				}
				tempDFN = searchEarliestDFN(i);

				if (tempDFN < minDFN) {
					minDFN = tempDFN;
				}
			}
		}

		// 如果没有子边，则搜索到的次序号就是它自身
		if (!hasSubEdge && DFN[label] != -1) {
			minDFN = DFN[label];
		}

		return minDFN;
	}
	
	/**
	 * 标准搜索强连通分量算法
	 */
	public void standardSearchSCG(){
		initDfnAndLow();
		
		verticStack.add(1);
		remainVertices.remove((Integer)1);
		//从标号为1的第一个节点开始搜索
		dfsSearchSCG(1);
		
		//输出结果中的强连通分量
		printSCG();
	}

	/**
	 * 深度优先搜索强连通分量
	 * 
	 * @param u
	 *            当前搜索的节点标号
	 */
	private void dfsSearchSCG(int u) {
		DFN[u] = currentSeq;
		LOW[u] = currentSeq;
		currentSeq++;

		for (int i = 1; i <graph.edges[u].length; i++) {
			// 判断u,i两节点是否相连
			if (graph.edges[u][i] == 1) {
				// 相连的情况下，当i未被访问过的时候，加入栈中
				if (remainVertices.contains(i)) {
					verticStack.add(i);
					remainVertices.remove((Integer) i);
					// 递归搜索
					dfsSearchSCG(i);
					LOW[u] = (LOW[u] < LOW[i] ? LOW[u] : LOW[i]);
				} else if(verticStack.contains(i)){
					// 如果已经访问过，并且还未出栈过的
					LOW[u] = (LOW[u] < DFN[i] ? LOW[u] : DFN[i]);
					//LOW[u] = (LOW[u] < LOW[i] ? LOW[u] : LOW[i]); 如果都用LOW做判断，也可以通过测试
				}
			}
		}

		// 最后判断DFN和LOW是否相等
		if (DFN[u] == LOW[u]) {
			popStackGraph(u);
		}
	}

	/**
	 * 输出有向图中的强连通分量
	 */
	private void printSCG() {
		int i = 1;
		String resultStr = "";
		System.out.println("所有强连通分量子图:");
		for (ArrayList<Integer> graph : resultGraph) {
			resultStr = "";
			resultStr += "强连通分量" + i + "：{";
			for (Integer v : graph) {
				resultStr += (v + ", ");
			}
			resultStr = (String) resultStr.subSequence(0,
					resultStr.length() - 2);
			resultStr += "}";

			System.out.println(resultStr);
			i++;
		}
	}
}
