package Simhash;

/**
 * 相似哈希算法
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//二进制哈希编码位数
		int hashBitNum;
		//相同位置占比最小阈值
		double minRate;
		String newsPath1;
		String newsPath2;
		String newsPath3;
		SimHashTool tool;
		
		hashBitNum = 32;
		//至少有一半的位置值相同
		minRate = 0.5;
		newsPath1 = "C:\\Users\\lyq\\Desktop\\icon\\test\\testNews1-split.txt";
		newsPath2 = "C:\\Users\\lyq\\Desktop\\icon\\test\\trainNews2-split.txt";
		newsPath3 = "C:\\Users\\lyq\\Desktop\\icon\\test\\trainNews1-split.txt";
		
		tool = new SimHashTool(hashBitNum, minRate);
		tool.compareArticals(newsPath1, newsPath2);
		tool.compareArticals(newsPath2, newsPath3);
	}
}
