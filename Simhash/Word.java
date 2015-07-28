package Simhash;

/**
 * 词语实体类
 * 
 * @author lyq
 * 
 */
public class Word {
	// 词语名称
	String name;
	// 词频
	double frequencyCount;

	public Word(String name, double frequencyCount) {
		this.name = name;
		this.frequencyCount = frequencyCount;
	}
}
