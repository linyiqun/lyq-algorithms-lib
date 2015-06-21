package TopK;

/**
 * 查询类
 * @author lyq
 *
 */
public class Query implements Comparable<Query>{
	//查询次数
	Integer count;
	//查询词
	String word;
	
	public Query(int count, String word){
		this.count = count;
		this.word = word;
	}

	@Override
	public int compareTo(Query o) {
		// TODO Auto-generated method stub
		return o.count.compareTo(this.count);
	}
	
	
}
