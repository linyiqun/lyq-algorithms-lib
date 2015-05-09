package TextMining.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TextMining.crawler.entity.Comment;
import TextMining.crawler.entity.Data;
import TextMining.crawler.entity.DataContainer;

import com.google.gson.Gson;

/**
 * 腾讯新闻爬虫工具类
 * 
 * @author lyq
 * 
 */
public class QQNewsCrawler {
	// 腾讯新闻评论链接url的格式
	public static final String NEWS_COMMENTS_URL_FORMAT = "http://coral.qq.com/article/{0}/comment?commentid={1}&reqnum={2}&tag=&callback=mainComment&_=1389623278900";

	// 腾讯新闻详情页的链接
	private String newsUrl;
	//评论数据输出路径
	private String outputPath;
	// 需要爬取的评论数总量
	private int totalCommentcount;
	// 每次请求的评论数，一次上限50条评论
	private int reqCommentNum;
	// 评论列表
	private ArrayList<Comment> commentLists;

	public QQNewsCrawler(String newUrl, int totalCommentcount, int reqCommentNum, String outputPath) {
		this.newsUrl = newUrl;
		this.totalCommentcount = totalCommentcount;
		this.outputPath = outputPath;
		
		if (reqCommentNum > 50) {
			// 每次请求最多只能50条
			reqCommentNum = 50;
		}
		this.reqCommentNum = reqCommentNum;
	}

	/**
	 * 从新闻详情页中爬取新闻标题和评论ID
	 * 
	 * @return
	 */
	public String[] crawlCmtIdAndTitle() {
		String[] array;
		String[] tempArray;
		// 页面HTML字符
		String htmlStr;
		String cmtId;
		String newsTitle;
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input2.txt";
		Pattern p;
		Matcher m;

		cmtId = null;
		newsTitle = null;
		array = new String[2];
		htmlStr = sendGet(newsUrl);
		// htmlStr = readDataFile(filePath);

		p = Pattern.compile("cmt_id = (.*);");
		m = p.matcher(htmlStr);

		while (m.find()) {
			cmtId = m.group();
			System.out.println(cmtId);
			break;
		}

		p = Pattern.compile("<title>(.*)</title>");
		m = p.matcher(htmlStr);

		while (m.find()) {
			newsTitle = m.group();
			System.out.println(newsTitle);
			break;
		}

		// 对匹配到的评论id字符做解析
		if (cmtId != null && !cmtId.equals("")) {
			tempArray = cmtId.split(";");
			cmtId = tempArray[0];
			tempArray = cmtId.split("=");
			cmtId = tempArray[1].trim();
			System.out.println(cmtId);
		}

		int pos1;
		int pos2;
		// 对匹配到的新闻标题做解析
		if (newsTitle != null && !newsTitle.equals("")) {
			pos1 = newsTitle.indexOf(">");
			pos2 = newsTitle.lastIndexOf("<");

			newsTitle = newsTitle.substring(pos1 + 1, pos2);
			System.out.println(newsTitle);
		}

		array[0] = cmtId;
		array[1] = newsTitle;

		return array;
	}

	/**
	 * 根据新闻评论ID爬取腾讯新闻评论数据
	 * 
	 * @throws
	 */
	public void crawlNewsComments() {
		String resultCommentStr;
		String requestUrl;
		String cmtId;
		String[] info;
		String startCommentId;
		int index1;
		int index2;
		// 当前获取到评论条数
		int currentCommentNum;
		int sleepTime;
		Random random;

		startCommentId = "";
		currentCommentNum = 0;
		random = new Random();
		commentLists = new ArrayList<>();

		info = crawlCmtIdAndTitle();
		cmtId = info[0];
		// cmtId = "1004703995";

		// 当请求总量达到要求的量时，跳出循环
		while (currentCommentNum < totalCommentcount) {
			requestUrl = MessageFormat.format(NEWS_COMMENTS_URL_FORMAT, cmtId,
					startCommentId, reqCommentNum);
			resultCommentStr = sendGet(requestUrl);

			// 截取出json格式的评论数据
			index1 = resultCommentStr.indexOf("{");
			index2 = resultCommentStr.lastIndexOf("}");
			resultCommentStr = resultCommentStr.substring(index1, index2 + 1);

			System.out.println(resultCommentStr);
			// 以上次最后一条评论的id为起始ID，继续爬取数据
			startCommentId = parseJSONData(resultCommentStr);

			// 如果解析出现异常，则立即退出
			if (startCommentId == null) {
				break;
			}

			try {
				// 随机睡眠1到5秒
				sleepTime = random.nextInt(5) + 1;
				Thread.sleep(1000 * sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			currentCommentNum += reqCommentNum;
		}

		// 最后将本次爬取的所有评论写入到文件中
		writeStringToFile(commentLists, outputPath);
	}

	/**
	 * 解析评论数据的json格式字符串
	 * 
	 * @param dataStr
	 *            json数据
	 * @return 返回此次获取的最后一条评论的id
	 */
	private String parseJSONData(String dataStr) {
		String lastId;
		Gson gson = new Gson();
		DataContainer dataContainer;
		Data data;
		ArrayList<Comment> cList;

		dataContainer = gson.fromJson(dataStr, DataContainer.class);
		// 如果获取数据异常，则返回控制
		if (dataContainer == null || dataContainer.getErrCode() != 0) {
			return null;
		}

		data = dataContainer.getData();
		//一旦发现已经没有数据了,则返回
		if (data == null) {
			return null;
		}

		cList = data.getCommentid();
		if(cList == null || cList.size() == 0){
			return null;
		}
		commentLists.addAll(cList);

		lastId = dataContainer.getData().getLast();

		return lastId;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @return URL 所代表远程资源的响应结果
	 */
	private String sendGet(String requestUrl) {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(requestUrl);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 从文件中读取数据
	 */
	private String readDataFile(String filePath) {
		File file = new File(filePath);
		String resultStr = "";

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			while ((str = in.readLine()) != null) {
				resultStr = resultStr + str;
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		return resultStr;
	}

	/**
	 * 写评论到目标文件中
	 * 
	 * @param resultStr
	 */
	public void writeStringToFile(ArrayList<Comment> commentList,
			String desFilePath) {
		File file;
		PrintStream ps;

		try {
			file = new File(desFilePath);
			ps = new PrintStream(new FileOutputStream(file));

			for (Comment c : commentList) {
				ps.println(c.getContent());// 往文件里写入字符串
			}

			ps.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
