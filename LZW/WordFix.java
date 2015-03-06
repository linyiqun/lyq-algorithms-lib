package LZW;

import java.util.HashMap;
import java.util.Map;

/**
 * 词组，包括前缀和后缀
 * 
 * @author lyq
 * 
 */
public class WordFix {
	// 词组前缀
	String prefix;
	// 词组后缀
	String suffix;
	
	// 编码词组映射表
	HashMap<WordFix, Integer> word2Code;

	public WordFix(String prefix, String suffix,
			HashMap<WordFix, Integer> word2Code) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.word2Code = word2Code;
	}

	/**
	 * 设置前缀
	 * 
	 * @param str
	 */
	public void setPrefix(String str) {
		this.prefix = str;
	}

	/**
	 * 设置后缀
	 * 
	 * @param str
	 */
	public void setSuffix(String str) {
		this.suffix = str;
	}

	/**
	 * 获取前缀字符
	 * 
	 * @return
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * 判断2个词组是否相等，比较前后字符是否相等
	 * 
	 * @param wf
	 * @return
	 */
	public boolean isSame(WordFix wf) {
		boolean isSamed = true;

		if (!this.prefix.equals(wf.prefix)) {
			isSamed = false;
		}

		if (!this.suffix.equals(wf.suffix)) {
			isSamed = false;
		}

		return isSamed;
	}

	/**
	 * 判断此词组是否已经被编码
	 * 
	 * @return
	 */
	public boolean hasWordCode() {
		boolean isContained = false;
		WordFix wf = null;

		for (Map.Entry entry : word2Code.entrySet()) {
			wf = (WordFix) entry.getKey();
			if (this.isSame(wf)) {
				isContained = true;
				break;
			}
		}

		return isContained;
	}

	/**
	 * 词组进行编码
	 * 
	 * @param wordCode
	 *            此词组将要被编码的值
	 */
	public void wordFixCoded(int wordCode) {
		word2Code.put(this, wordCode);
	}

	/**
	 * 读入后缀字符
	 * 
	 * @param str
	 */
	public void readSuffix(String str) {
		int code = 0;
		boolean isCoded = false;
		WordFix wf = null;

		for (Map.Entry entry : word2Code.entrySet()) {
			code = (int) entry.getValue();
			wf = (WordFix) entry.getKey();
			if (this.isSame(wf)) {
				isCoded = true;
				// 编码变为前缀
				this.prefix = code + "";
				break;
			}
		}

		if (!isCoded) {
			return;
		}
		this.suffix = str;
	}

	/**
	 * 将词组转为连续的字符形式
	 * 
	 * @return
	 */
	public String transToStr() {
		int code = 0;
		String currentPrefix = this.prefix;
		
		for(Map.Entry entry: word2Code.entrySet()){
			code = (int) entry.getValue();
			//如果前缀字符还是编码，继续解析
			if(currentPrefix.equals(code + "")){
				currentPrefix =((WordFix) entry.getKey()).transToStr();
				break;
			}
		}
		
		return currentPrefix + this.suffix;
	}

}
