package TextMining.crawler.entity;

import java.util.ArrayList;

/**
 * 总评论实体
 * 
 * @author lyq
 * 
 */
public class Data {
	// 对应的新闻ID
	private String targetid;
	// 此新闻的评论总数
	private int total;
	// 当前获取数据评论中的首条评论子id
	private String first;
	// 当前获取数据评论中的末尾评论子id
	private String last;
	// 判断在此数据后面还有没有评论数据
	private boolean hasnext;
	// 具体子评论列表
	private ArrayList<Comment> commentid;

	public String getTargetid() {
		return targetid;
	}

	public void setTargetid(String targetid) {
		this.targetid = targetid;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public boolean isHasnext() {
		return hasnext;
	}

	public void setHasnext(boolean hasnext) {
		this.hasnext = hasnext;
	}

	public ArrayList<Comment> getCommentid() {
		return commentid;
	}

	public void setCommentid(ArrayList<Comment> commentid) {
		this.commentid = commentid;
	}
}
