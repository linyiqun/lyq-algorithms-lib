package LZW;

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
 * LZW解压缩算法工具类
 * 
 * @author lyq
 * 
 */
public class LZWTool {
	// 开始的编码的编码号从256开始
	public static int LZW_CODED_NUM = 256;

	// 待压缩文件地址
	private String srcFilePath;
	// 目标文件地址
	private String desFileLoc;
	// 压缩后的目标文件名
	private String desFileName;
	// 结果字符，将被写到输出文件中
	private String resultStr;
	// 编码词组映射表
	HashMap<WordFix, Integer> word2Code;
	// 源文件数据
	private ArrayList<String> totalDatas;

	public LZWTool(String srcFilePath, String desFileLoc, String desFileName) {
		this.srcFilePath = srcFilePath;
		this.desFileLoc = desFileLoc;
		this.desFileName = desFileName;

		word2Code = new HashMap<>();
		totalDatas = new ArrayList<>();
		readDataFile(totalDatas);
	}

	/**
	 * 从文件中读取数据
	 * 
	 * @param inputData
	 *            输入数据容器
	 */
	private void readDataFile(ArrayList<String> inputData) {
		File file = new File(srcFilePath);
		ArrayList<String[]> dataArray = new ArrayList<String[]>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = new String[str.length()];
				for (int i = 0; i < str.length(); i++) {
					tempArray[i] = str.charAt(i) + "";
				}

				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		System.out.print("压缩前的字符：");
		for (String[] array : dataArray) {
			for (String s : array) {
				inputData.add(s);
				System.out.print(s);
			}
		}
		System.out.println();
	}

	/**
	 * 进行lzw压缩
	 */
	public void compress() {
		resultStr = "";
		boolean existCoded = false;
		String prefix = totalDatas.get(0);
		WordFix wf = null;

		for (int i = 1; i < totalDatas.size(); i++) {
			wf = new WordFix(prefix, totalDatas.get(i), word2Code);
			existCoded = false;

			// 如果当前词组存在相应编码，则继续读入后缀
			while (wf.hasWordCode()) {
				i++;
				// 如果到底了则跳出循环
				if (i == totalDatas.size()) {
					// 说明还存在词组编码的
					existCoded = true;
					wf.readSuffix("");
					break;
				}

				wf.readSuffix(totalDatas.get(i));
			}

			if (!existCoded) {
				// 对未编码过的词组进行编码
				wf.wordFixCoded(LZW_CODED_NUM);
				LZW_CODED_NUM++;
			}

			// 将前缀输出
			resultStr += wf.getPrefix() + ",";
			// 后缀边前缀
			prefix = wf.suffix;
		}

		// 将原词组的后缀加入也就是新的词组的前缀
		resultStr += prefix;
		System.out.println("压缩后的字符：" + resultStr);
		writeStringToFile(resultStr, desFileLoc + desFileName);
	}

	public void unCompress(String srcFilePath, String desFilePath) {
		String result = "";
		int code = 0;

		File file = new File(srcFilePath);
		ArrayList<String[]> datas = new ArrayList<String[]>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(",");
				datas.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		for (String[] array : datas) {
			for (String s : array) {
				for (Map.Entry entry : word2Code.entrySet()) {
					code = (int) entry.getValue();
					if (s.equals(code + "")) {
						s = ((WordFix) entry.getKey()).transToStr();
						break;
					}
				}

				result += s;
			}
		}

		System.out.println("解压后的字符：" + result);
		writeStringToFile(result, desFilePath);
	}

	/**
	 * 写字符串到目标文件中
	 * 
	 * @param resultStr
	 */
	public void writeStringToFile(String resultStr, String desFilePath) {
		try {
			File file = new File(desFilePath);
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.println(resultStr);// 往文件里写入字符串
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
