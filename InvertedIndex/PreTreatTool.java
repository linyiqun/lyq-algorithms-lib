package InvertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文档预处理工具类
 * 
 * @author lyq
 * 
 */
public class PreTreatTool {
	// 一些无具体意义的过滤词
	public static String[] FILTER_WORDS = new String[] { "at", "At", "The",
			"the", "is", "very" };

	// 批量文档的文件地址
	private ArrayList<String> docFilePaths;
	// 输出的有效词的存放路径
	private ArrayList<String> effectWordPaths;

	public PreTreatTool(ArrayList<String> docFilePaths) {
		this.docFilePaths = docFilePaths;
	}

	/**
	 * 获取文档有效词文件路径
	 * 
	 * @return
	 */
	public ArrayList<String> getEFWPaths() {
		return this.effectWordPaths;
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
	 * 对文档内容词汇进行预处理
	 */
	public void preTreatWords() {
		String baseOutputPath = "";
		int endPos = 0;
		ArrayList<String> tempWords = null;
		effectWordPaths = new ArrayList<>();

		for (String filePath : docFilePaths) {
			tempWords = readDataFile(filePath);
			filterWords(tempWords, true);

			// 重新组装出新的输出路径
			endPos = filePath.lastIndexOf(".");
			baseOutputPath = filePath.substring(0, endPos);

			writeOutOperation(tempWords, baseOutputPath + "-efword.txt");
			effectWordPaths.add(baseOutputPath + "-efword.txt");
		}
	}

	/**
	 * 
	 * 对文档中的词语进行过滤操作
	 * 
	 * @param words
	 *            待处理文档词语
	 * @param canRepeated
	 *            有效词是否可以重复
	 */
	private void filterWords(ArrayList<String> words, boolean canRepeated) {
		boolean isFilterWord;
		// 做形容词匹配
		Pattern adjPattern;
		// 做动词时态的匹配
		Pattern formerPattern;
		// 数字匹配
		Pattern numberPattern;
		Matcher adjMatcher;
		Matcher formerMatcher;
		Matcher numberMatcher;
		ArrayList<String> deleteWords = new ArrayList<>();

		adjPattern = Pattern.compile(".*(ly$|ful$|ing$)");
		formerPattern = Pattern.compile(".*ed$");
		numberPattern = Pattern.compile("[0-9]+(.[0-9]+)?");

		String w;
		for (int i = 0; i < words.size(); i++) {
			w = words.get(i);
			isFilterWord = false;

			for (String fw : FILTER_WORDS) {
				if (fw.equals(w)) {
					deleteWords.add(w);
					isFilterWord = true;
					break;
				}
			}

			if (isFilterWord) {
				continue;
			}

			adjMatcher = adjPattern.matcher(w);
			formerMatcher = formerPattern.matcher(w);
			numberMatcher = numberPattern.matcher(w);

			// 将词语统一小写字母化
			w = w.toLowerCase();

			// 如果是形容词,副词形式的或是纯数字的词，则进行过滤
			if (adjMatcher.matches() || numberMatcher.matches()) {
				deleteWords.add(w);
			} else if (formerMatcher.matches()) {
				// 如果是ed结尾表明是动词的在时态方面的变化，进行变化，转为原有动词的形式，截去最末尾2个额外添加的后缀词
				w = w.substring(0, w.length() - 2);
			}
			
			words.set(i, w);
		}

		// 进行无效词的过滤
		words.removeAll(deleteWords);
		deleteWords.clear();

		String s1;
		String s2;

		// 进行词语的去重
		for (int i = 0; i < words.size() - 1; i++) {
			s1 = words.get(i);

			for (int j = i + 1; j < words.size(); j++) {
				s2 = words.get(j);

				// 找到存在相同的词了，就挑出循环
				if (s1.equals(s2)) {
					deleteWords.add(s1);
					break;
				}
			}
		}

		// 删除多余重复的词语
		words.removeAll(deleteWords);
		words.addAll(deleteWords);
	}

	/**
	 * 将数据写出到磁盘文件操作，如果文件已经存在，则在文件尾部进行内容追加
	 * 
	 * @param buffer
	 *            当前写缓冲中的数据
	 * @param filePath
	 *            输出地址
	 */
	private void writeOutOperation(ArrayList<String> buffer, String filePath) {
		StringBuilder strBuilder = new StringBuilder();

		// 将缓冲中的数据组成字符写入到文件中
		for (String word : buffer) {
			strBuilder.append(word);
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

}
