package TopK;

/**
 * 哈希函数工具类，提供了3个字符哈希函数
 * @author lyq
 *
 */
public class HashTool {
	/**
	 * BKDR字符哈希算法
	 * 
	 * @param str
	 * @return
	 */
	public static long BKDRHash(String str) {
		int seed = 31; /* 31 131 1313 13131 131313 etc.. */
		long hash = 0;
		int i = 0;

		for (i = 0; i < str.length(); i++) {
			hash = (hash * seed) + (str.charAt(i));
		}

		hash = Math.abs(hash);
		return hash;
	}

	/**
	 * SDB字符哈希算法
	 * 
	 * @param str
	 * @return
	 */
	public static long SDBMHash(String str) {
		long hash = 0;
		int i = 0;
		
		for (i = 0; i < str.length(); i++) {
			hash = (str.charAt(i)) + (hash << 6) + (hash << 16) - hash;
		}

		hash = Math.abs(hash);
		return hash;
	}

	/**
	 * DJB字符哈希算法
	 * 
	 * @param str
	 * @return
	 */
	public static long DJBHash(String str) {
		long hash = 5381;
		int i = 0;

		for (i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + (str.charAt(i));
		}

		hash = Math.abs(hash);
		return hash;
	}

}
