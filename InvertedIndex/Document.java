package InvertedIndex;

import java.util.ArrayList;

/**
 * 文档类
 * @author lyq
 *
 */
public class Document {
	//文档的唯一标识
	int docId;
	//文档的文件地址
	String filePath;
	//文档中的有效词
	ArrayList<String> effectWords;
	
	public Document(ArrayList<String> effectWords, String filePath){
		this.effectWords = effectWords;
		this.filePath = filePath;
	}
	
	public Document(ArrayList<String> effectWords, String filePath, int docId){
		this(effectWords, filePath);
		this.docId = docId;
	}
}
