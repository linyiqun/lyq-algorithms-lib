package TextMining.crawler.entity;

/**
 * 数据外层包装类
 * 
 * @author lyq
 * 
 */
public class DataContainer {
	// 请求回应码
	private int errCode;
	// 主题数据类
	private Data data;

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
}
