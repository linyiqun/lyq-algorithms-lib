package Logistic;

/**
 * 逻辑斯蒂模型公式
 * 
 * @author lyq
 * 
 */
public class LogisticTool {
	/**
	 * 逻辑斯蒂回归模型
	 * 
	 * @param x
	 *            自变量的值
	 * @param param
	 *            计算好的参数数组
	 * @return
	 */
	public double logisticRegression(int x, double... param) {
		double temp;
		double pro = 0;

		// 首先e的次方和
		temp = param[0];
		for (int i = 1; i < param.length; i++) {
			temp += param[i] * x;
		}

		// 调用逻辑斯蒂回归方程进行计算
		pro = Math.exp(temp) / (1 + Math.exp(temp));

		return pro;
	}

	/**
	 * 逻辑斯蒂增长模型在信息传播问题上的应用
	 * 
	 * @param B
	 *            参数值
	 * @param b
	 *            参数值
	 * @param t
	 *            消息传播的时间
	 * @return
	 */
	public double logisticMessageSpread(double B, double b, double t) {
		double pro;

		pro = 1 / (1 + B * Math.exp(-b * t));

		return pro;
	}

	/**
	 * 逻辑斯蒂模型在销量预测上的应用
	 * 
	 * @param a
	 *            分母参数
	 * @param B
	 *            参数值
	 * @param b
	 *            参数值
	 * @param t
	 *            销量预测时间
	 * @return
	 */
	public double logisticSalingPredict(double a, double B, double b, double t) {
		double pro;

		pro = a / (1 + B * Math.exp(-b * t));

		return pro;
	}
}
