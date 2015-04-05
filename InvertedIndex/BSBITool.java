package InvertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * BSBI基于磁盘的外部排序算法
 * 
 * @author lyq
 * 
 */
public class BSBITool {
	// 文档唯一标识ID
	public static int DOC_ID = 0;

	// 读缓冲区的大小
	private int readBufferSize;
	// 写缓冲区的大小
	private int writeBufferSize;
	// 读入的文档的有效词文件地址
	private ArrayList<String> effectiveWordFiles;
	// 倒排索引输出文件地址
	private String outputFilePath;
	// 读缓冲 1
	private String[][] readBuffer1;
	// 读缓冲2
	private String[][] readBuffer2;
	// 写缓冲区
	private String[][] writeBuffer;
	// 有效词与hashcode的映射
	private Map<String, String> code2word;

	public BSBITool(ArrayList<String> effectiveWordFiles, int readBufferSize,
			int writeBufferSize) {
		this.effectiveWordFiles = effectiveWordFiles;
		this.readBufferSize = readBufferSize;
		this.writeBufferSize = writeBufferSize;

		initBuffers();
	}

	/**
	 * 初始化缓冲区的设置
	 */
	private void initBuffers() {
		readBuffer1 = new String[readBufferSize][2];
		readBuffer2 = new String[readBufferSize][2];
		writeBuffer = new String[writeBufferSize][2];
	}

	/**
	 * 从文件中读取有效词并进行编码替换
	 * 
	 * @param filePath
	 *            返回文档
	 */
	private Document readEffectWords(String filePath) {
		long hashcode = 0;

		String w;
		Document document;
		code2word = new HashMap<String, String>();
		ArrayList<String> words;

		words = readDataFile(filePath);

		for (int i = 0; i < words.size(); i++) {
			w = words.get(i);

			hashcode = BKDRHash(w);
			hashcode = hashcode % 10000;

			// 将有效词的hashcode取模值作为对应的代表
			code2word.put(hashcode + "", w);
			w = hashcode + "";

			words.set(i, w);
		}

		document = new Document(words, filePath, DOC_ID);
		DOC_ID++;

		return document;
	}

	/**
	 * 将字符做哈希值的转换
	 * 
	 * @param str
	 *            待转换字符
	 * @return
	 */
	private long BKDRHash(String str) {
		int seed = 31; /* 31 131 1313 13131 131313 etc.. */
		long hash = 0;
		int i = 0;

		for (i = 0; i < str.length(); i++) {
			hash = (hash * seed) + (str.charAt(i));
		}

		return hash;

	}

	/**
	 * 根据输入的有效词输出倒排索引文件
	 */
	public void outputInvertedFiles() {
		int index = 0;
		String baseFilePath = "";
		outputFilePath = "";
		Document doc;
		ArrayList<String> tempPaths;
		ArrayList<String[]> invertedData1;
		ArrayList<String[]> invertedData2;

		tempPaths = new ArrayList<>();
		for (String filePath : effectiveWordFiles) {
			doc = readEffectWords(filePath);
			writeOutFile(doc);

			index = doc.filePath.lastIndexOf(".");
			baseFilePath = doc.filePath.substring(0, index);
			writeOutOperation(writeBuffer, baseFilePath + "-temp.txt");

			tempPaths.add(baseFilePath + "-temp.txt");
		}

		outputFilePath = baseFilePath + "-bsbi-inverted.txt";

		// 将中间产生的倒排索引数据进行总的合并并输出到一个文件中
		for (int i = 1; i < tempPaths.size(); i++) {
			if (i == 1) {
				invertedData1 = readInvertedFile(tempPaths.get(0));
			} else {
				invertedData1 = readInvertedFile(outputFilePath);
			}

			invertedData2 = readInvertedFile(tempPaths.get(i));

			mergeInvertedData(invertedData1, invertedData2, false,
					outputFilePath);

			writeOutOperation(writeBuffer, outputFilePath, false);
		}
	}

	/**
	 * 将文档的最终的倒排索引结果写出到文件
	 * 
	 * @param doc
	 *            待处理文档
	 */
	private void writeOutFile(Document doc) {
		// 在读缓冲区中是否需要再排序
		boolean ifSort = true;
		int index = 0;
		String baseFilePath;
		String[] temp;
		ArrayList<String> tempWords = (ArrayList<String>) doc.effectWords
				.clone();
		ArrayList<String[]> invertedData1;
		ArrayList<String[]> invertedData2;

		invertedData1 = new ArrayList<>();
		invertedData2 = new ArrayList<>();

		// 将文档的数据平均拆分成2份，用于读入后面的2个缓冲区中
		for (int i = 0; i < tempWords.size() / 2; i++) {
			temp = new String[2];
			temp[0] = tempWords.get(i);
			temp[1] = doc.docId + "";
			invertedData1.add(temp);

			temp = new String[2];
			temp[0] = tempWords.get(i + tempWords.size() / 2);
			temp[1] = doc.docId + "";
			invertedData2.add(temp);
		}

		// 如果是奇数个，则将最后一个补入
		if (tempWords.size() % 2 == 1) {
			temp = new String[2];
			temp[0] = tempWords.get(tempWords.size() - 1);
			temp[1] = doc.docId + "";
			invertedData2.add(temp);
		}

		index = doc.filePath.lastIndexOf(".");
		baseFilePath = doc.filePath.substring(0, index);
		mergeInvertedData(invertedData1, invertedData2, ifSort, baseFilePath
				+ "-temp.txt");
	}

	/**
	 * 合并读缓冲区数据写到写缓冲区中，用到了归并排序算法
	 * 
	 * @param outputPath
	 *            写缓冲区的写出的路径
	 */
	private void mergeWordBuffers(String outputPath) {
		int i = 0;
		int j = 0;
		int num1 = 0;
		int num2 = 0;
		// 写缓冲区下标
		int writeIndex = 0;

		while (readBuffer1[i][0] != null && readBuffer2[j][0] != null) {
			num1 = Integer.parseInt(readBuffer1[i][0]);
			num2 = Integer.parseInt(readBuffer2[j][0]);

			// 如果缓冲1小，则优先存缓冲1到写缓冲区中
			if (num1 < num2) {
				writeBuffer[writeIndex][0] = num1 + "";
				writeBuffer[writeIndex][1] = readBuffer1[i][1];

				i++;
			} else if (num2 < num1) {
				writeBuffer[writeIndex][0] = num2 + "";
				writeBuffer[writeIndex][1] = readBuffer1[j][1];

				j++;
			} else if (num1 == num2) {
				// 如果两个缓冲区中的数字一样，说明是同个有效词，先进行合并再写入
				writeBuffer[writeIndex][0] = num1 + "";
				writeBuffer[writeIndex][1] = readBuffer1[i][1] + ":"
						+ readBuffer2[j][1];

				i++;
				j++;
			}

			// 写的指针往后挪一位
			writeIndex++;

			// 如果写满写缓冲区时，进行写出到文件操作
			if (writeIndex >= writeBufferSize) {
				writeOutOperation(writeBuffer, outputPath);
				writeIndex = 0;
			}
		}

		if (readBuffer1[i][0] == null) {
			writeRemainReadBuffer(readBuffer2, j, outputPath);
		}

		if (readBuffer2[j][0] == null) {
			writeRemainReadBuffer(readBuffer1, j, outputPath);
		}
	}

	/**
	 * 将数据写出到磁盘文件操作，如果文件已经存在，则在文件尾部进行内容追加
	 * 
	 * @param buffer
	 *            当前写缓冲中的数据
	 * @param filePath
	 *            输出地址
	 */
	private void writeOutOperation(String[][] buffer, String filePath) {
		String word;
		StringBuilder strBuilder = new StringBuilder();

		// 将缓冲中的数据组成字符写入到文件中
		for (String[] array : buffer) {
			if (array[0] == null) {
				continue;
			}

			word = array[0];

			strBuilder.append(word);
			strBuilder.append(" ");
			strBuilder.append(array[1]);
			strBuilder.append("\n");
		}

		try {
			File file = new File(filePath);
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.print(strBuilder.toString());// 往文件里写入字符串
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 将数据写出到磁盘文件操作，如果文件已经存在，则在文件尾部进行内容追加
	 * 
	 * @param buffer
	 *            当前写缓冲中的数据
	 * @param filePath
	 *            输出地址
	 * @param isCoded
	 *            是否以编码的方式输出
	 */
	private void writeOutOperation(String[][] buffer, String filePath, boolean isCoded) {
		String word;
		StringBuilder strBuilder = new StringBuilder();

		// 将缓冲中的数据组成字符写入到文件中
		for (String[] array : buffer) {
			if (array[0] == null) {
				continue;
			}

			if(!isCoded){
				word = code2word.get(array[0]);
			}else{
				word = array[0];
			}

			strBuilder.append(word);
			strBuilder.append(" ");
			strBuilder.append(array[1]);
			strBuilder.append("\n");
		}

		try {
			File file = new File(filePath);
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.print(strBuilder.toString());// 往文件里写入字符串
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 将剩余的读缓冲区中的数据读入写缓冲区中
	 * 
	 * @param remainBuffer
	 *            读缓冲区的剩余缓冲
	 * @param currentReadPos
	 *            当前的读取位置
	 * @param outputPath
	 *            写缓冲区的写出文件路径
	 */
	private void writeRemainReadBuffer(String[][] remainBuffer,
			int currentReadPos, String outputPath) {
		while (remainBuffer[currentReadPos][0] != null
				&& currentReadPos < readBufferSize) {
			removeRBToWB(remainBuffer[currentReadPos]);

			currentReadPos++;

			// 如果写满写缓冲区时，进行写出到文件操作
			if (writeBuffer[writeBufferSize - 1][0] != null) {
				writeOutOperation(writeBuffer, outputPath);
			}
		}

	}

	/**
	 * 将剩余读缓冲区中的数据通过插入排序的方式插入写缓冲区
	 * 
	 * @param record
	 */
	private void removeRBToWB(String[] record) {
		int insertIndex = 0;
		int endIndex = 0;
		long num1;
		long num2;
		long code = Long.parseLong(record[0]);

		// 如果写缓冲区目前为空，则直接加入
		if (writeBuffer[0][0] == null) {
			writeBuffer[0] = record;
			return;
		}

		// 寻找待插入的位置
		for (int i = 0; i < writeBufferSize - 1; i++) {
			if (writeBuffer[i][0] == null) {
				endIndex = i;
				break;
			}

			num1 = Long.parseLong(writeBuffer[i][0]);

			if (writeBuffer[i + 1][0] == null) {
				if (code > num1) {
					endIndex = i + 1;
					insertIndex = i + 1;
				}
			} else {
				num2 = Long.parseLong(writeBuffer[i + 1][0]);

				if (code > num1 && code < num2) {
					insertIndex = i + 1;
				}
			}
		}

		// 进行插入操作，相关数据进行位置迁移
		for (int i = endIndex; i > insertIndex; i--) {
			writeBuffer[i] = writeBuffer[i - 1];
		}
		writeBuffer[insertIndex] = record;
	}

	/**
	 * 将磁盘中的2个倒排索引数据进行合并
	 * 
	 * @param invertedData1
	 *            倒排索引为文件数据1
	 * @param invertedData2
	 *            倒排索引文件数据2
	 * @param isSort
	 *            是否需要对缓冲区中的数据进行排序
	 * @param outputPath
	 *            倒排索引输出文件地址
	 */
	private void mergeInvertedData(ArrayList<String[]> invertedData1,
			ArrayList<String[]> invertedData2, boolean ifSort, String outputPath) {
		int rIndex1 = 0;
		int rIndex2 = 0;

		// 重新初始化缓冲区
		initBuffers();

		while (invertedData1.size() > 0 && invertedData2.size() > 0) {
			readBuffer1[rIndex1][0] = invertedData1.get(0)[0];
			readBuffer1[rIndex1][1] = invertedData1.get(0)[1];

			readBuffer2[rIndex2][0] = invertedData2.get(0)[0];
			readBuffer2[rIndex2][1] = invertedData2.get(0)[1];

			invertedData1.remove(0);
			invertedData2.remove(0);
			rIndex1++;
			rIndex2++;

			if (rIndex1 == readBufferSize) {
				if (ifSort) {
					wordBufferSort(readBuffer1);
					wordBufferSort(readBuffer2);
				}

				mergeWordBuffers(outputPath);
				initBuffers();
			}
		}

		if (ifSort) {
			wordBufferSort(readBuffer1);
			wordBufferSort(readBuffer2);
		}

		mergeWordBuffers(outputPath);
		readBuffer1 = new String[readBufferSize][2];
		readBuffer2 = new String[readBufferSize][2];

		if (invertedData1.size() == 0 && invertedData2.size() > 0) {
			readRemainDataToRB(invertedData2, outputPath);
		} else if (invertedData1.size() > 0 && invertedData2.size() == 0) {
			readRemainDataToRB(invertedData1, outputPath);
		}
	}

	/**
	 * 剩余的有效词数据读入读缓冲区
	 * 
	 * @param remainData
	 *            剩余数据
	 * @param outputPath
	 *            输出文件路径
	 */
	private void readRemainDataToRB(ArrayList<String[]> remainData,
			String outputPath) {
		int rIndex = 0;
		while (remainData.size() > 0) {
			readBuffer1[rIndex][0] = remainData.get(0)[0];
			readBuffer1[rIndex][1] = remainData.get(0)[1];
			remainData.remove(0);

			rIndex++;

			// 读缓冲 区写满，进行写入到写缓冲区中
			if (readBuffer1[readBufferSize - 1][0] != null) {
				wordBufferSort(readBuffer1);

				writeRemainReadBuffer(readBuffer1, 0, outputPath);
				initBuffers();
			}
		}

		wordBufferSort(readBuffer1);

		writeRemainReadBuffer(readBuffer1, 0, outputPath);

	}

	/**
	 * 缓冲区数据进行排序
	 * 
	 * @param buffer
	 *            缓冲空间
	 */
	private void wordBufferSort(String[][] buffer) {
		String[] temp;
		int k = 0;

		long num1 = 0;
		long num2 = 0;
		for (int i = 0; i < buffer.length - 1; i++) {
			// 缓冲区可能没填满
			if (buffer[i][0] == null) {
				continue;
			}

			k = i;
			for (int j = i + 1; j < buffer.length; j++) {
				// 缓冲区可能没填满
				if (buffer[j][0] == null) {
					continue;
				}
				// 获取2个缓冲区小块的起始编号值
				num1 = Long.parseLong(buffer[k][0]);
				num2 = Long.parseLong(buffer[j][0]);

				if (num2 < num1) {
					k = j;
				}
			}

			if (k != i) {
				temp = buffer[k];
				buffer[k] = buffer[i];
				buffer[i] = temp;
			}
		}
	}

	/**
	 * 从文件中读取倒排索引数据
	 * 
	 * @param filePath
	 *            单个文件
	 */
	private ArrayList<String[]> readInvertedFile(String filePath) {
		File file = new File(filePath);
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

		return dataArray;
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
				if (!word.equals("")) {
					words.add(word);
				}
			}
		}

		return words;
	}
}
