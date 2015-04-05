package InvertedIndex;

import java.util.ArrayList;

/**
 * 倒排索引测试类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//读写缓冲区的大小
		int readBufferSize;
		int writeBufferSize;
		String baseFilePath;
		PreTreatTool preTool;
		//BSBI基于磁盘的外部排序算法
		BSBITool bTool;
		//SPIMI内存式单边扫描构建算法
		SPIMITool sTool;
		//有效词文件路径
		ArrayList<String> efwFilePaths;
		ArrayList<String> docFilePaths;
		
		readBufferSize = 10;
		writeBufferSize = 20;
		baseFilePath = "C:\\Users\\lyq\\Desktop\\icon\\";
		docFilePaths = new ArrayList<>();
		docFilePaths.add(baseFilePath + "doc1.txt");
		docFilePaths.add(baseFilePath + "doc2.txt");
		
		//文档预处理工具类
		preTool = new PreTreatTool(docFilePaths);
		preTool.preTreatWords();
		
		//预处理完获取有效词文件路径
		efwFilePaths = preTool.getEFWPaths();
		bTool = new BSBITool(efwFilePaths, readBufferSize, writeBufferSize);
		bTool.outputInvertedFiles();
		
		sTool = new SPIMITool(efwFilePaths);
		sTool.createInvertedIndexFile();
	}
}
