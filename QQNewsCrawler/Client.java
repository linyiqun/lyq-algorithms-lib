package TextMining.crawler;

/**
 * 腾讯新闻爬虫程序测试类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//每次评论请求数量
		int reqNum;
		//总评论数
		int totalCommentCount;
		//评论的输出路径
		String outputPath;
		//腾讯新闻页url链接
		String newsUrl;
		QQNewsCrawler crawler;
		
		reqNum = 50;
		totalCommentCount = 100;
		newsUrl = "http://news.qq.com/a/20150508/004453.htm";
		outputPath = "C:\\Users\\lyq\\Desktop\\我的毕业设计\\newsComments2.txt";
		
		crawler = new QQNewsCrawler(newsUrl, totalCommentCount, reqNum, outputPath);
		crawler.crawlNewsComments();
	}
}
