package InvertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * SPIMI内存式单边扫描构建算法
 * @author lyq
 *
 */
public class SPIMITool {
	//倒排索引输出文件地址
	private String outputFilePath;
	// 读入的文档的有效词文件地址
	private ArrayList<String> effectiveWordFiles;
	// 内存缓冲区，不够还能够在增加空间
	private ArrayList<String[]> buffers;
	
	public SPIMITool(ArrayList<String> effectiveWordFiles){
		this.effectiveWordFiles = effectiveWordFiles;
	}
	
	/**
	 * 从文件中读取数据
	 * 
	 * @param filePath
	 *            单个文件
	 */
	private ArrayList<String> readDataFile(String filePath) {
		File file = new File(filePath);
		ArrayList<String[]> dataArray = new ArrayList<String[]>();
		ArrayList<String> words = new ArrayList<>();

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

		// 将每行词做拆分加入到总列表容器中
		for (String[] array : dataArray) {
			for (String word : array) {
				words.add(word);
			}
		}

		return words;
	}
 
	
	/**
	 * 根据已有的文档数据进行倒排索引文件的构建
	 * @param docs
	 * 文档集合
	 */
	private void writeInvertedIndex(ArrayList<Document> docs){
		ArrayList<String> datas;
		String[] recordData;
		
		buffers = new ArrayList<>();
		for(Document tempDoc: docs){
			datas = tempDoc.effectWords;
			
			for(String word: datas){
				recordData = new String[2];
				recordData[0] = word;
				recordData[1] = tempDoc.docId + "";
				
				addRecordToBuffer(recordData);
			}
		}
		
		//最后将数据写出到磁盘中
		writeOutOperation(buffers, outputFilePath);
	}
	
	/**
	 * 将新读入的数据记录读入到内存缓冲中，如果存在则加入到倒排记录表中
	 * @param insertedData
	 * 待插入的数据
	 */
	private void addRecordToBuffer(String[] insertedData){
		boolean isContained = false;
		String wordName;
		
		wordName = insertedData[0];
		for(String[] array: buffers){
			if(array[0].equals(wordName)){
				isContained = true;
				//添加倒排索引记录，以：隔开
				array[1] += ":" + insertedData[1];
				
				break;
			}
		}
		
		//如果没有包含，则说明是新的数据,直接添加
		if(!isContained){
			buffers.add(insertedData);
		}
	}
	
	/**
	 * 将数据写出到磁盘文件操作，如果文件已经存在，则在文件尾部进行内容追加
	 * @param buffer
	 * 当前写缓冲中的数据
	 * @param filePath
	 * 输出地址
	 */
	private void writeOutOperation(ArrayList<String[]> buffer, String filePath) {
		StringBuilder strBuilder = new StringBuilder();
		
		//将缓冲中的数据组成字符写入到文件中
		for(String[] array: buffer){
			strBuilder.append(array[0]);
			strBuilder.append(" ");
			strBuilder.append(array[1]);
			strBuilder.append("\n");
		}
		
		try {
			File file = new File(filePath);
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.println(strBuilder.toString());// 往文件里写入字符串
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 构造倒排索引文件
	 */
	public void createInvertedIndexFile(){
		int docId = 1;
		String baseFilePath;
		String fileName;
		String p;
		int index1 = 0;
		int index2 = 0;
		Document tempDoc;
		ArrayList<String> words;
		ArrayList<Document> docs;
		
		outputFilePath = "spimi";
		docs = new ArrayList<>();
		p = effectiveWordFiles.get(0);
		//提取文件名称
		index1 = p.lastIndexOf("\\");
		baseFilePath = p.substring(0, index1+1);
		outputFilePath = baseFilePath + "spimi";
		
		for(String path: effectiveWordFiles){
			//获取文档有效词
			words = readDataFile(path);
			tempDoc = new Document(words, path, docId);
			
			docId++;
			docs.add(tempDoc);
			
			//提取文件名称
			index1 = path.lastIndexOf("\\");
			index2 = path.lastIndexOf(".");
			fileName = path.substring(index1+1, index2);
			
			outputFilePath += "-" + fileName;
		}
		outputFilePath += ".txt";
		
		//根据文档数据进行倒排索引文件的创建
		writeInvertedIndex(docs);
	}

}
