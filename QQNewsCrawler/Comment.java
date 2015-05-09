package TextMining.crawler.entity;

/**
 * 具体的单条评论类
 * 
 * @author lyq
 * 
 */
public class Comment {
	// 代表的是此评论的ID
	private String id;
	// 评论对应的新闻ID
	private String targetid;
	// 评论的时间
	private String time;
	// 评论的具体内容
	private String content;
	// 评论被顶的次数
	private String up;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTargetid() {
		return targetid;
	}

	public void setTargetid(String targetid) {
		this.targetid = targetid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUp() {
		return up;
	}

	public void setUp(String up) {
		this.up = up;
	}

}
