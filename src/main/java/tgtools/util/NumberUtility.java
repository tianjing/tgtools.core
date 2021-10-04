package tgtools.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

import tgtools.exceptions.APPRuntimeException;
/**
 * @author tianjing
 */
public class NumberUtility {
	/**
	 * 将Number转换为BigDecimal
	 * 
	 * @param pNumber
	 * @return
	 */
	public static BigDecimal toBigDecimal(Number pNumber) {
		if ((pNumber instanceof BigDecimal)) {
			return (BigDecimal) pNumber;
		}
		if ((pNumber instanceof Integer)) {
			return new BigDecimal(((Integer) pNumber).intValue());
		}
		if ((pNumber instanceof Double)) {
			return BigDecimal.valueOf(((Double) pNumber).doubleValue());
		}
		if ((pNumber instanceof Float)) {
			return BigDecimal.valueOf(((Float) pNumber).doubleValue());
		}
		throw new APPRuntimeException(String.format(
				"不支持将数据类型[%1$s]转换为BigDecimal类型。", new Object[] { pNumber
						.getClass().getName() }));
	}

	/**
	 * 将Number转换为Integer （去掉小数点）
	 * 
	 * @param pValue
	 * @return
	 */
	public static Number toInteger(Number pValue) {
		String s = pValue.toString();
		if (s.matches("(\\d+)\\.0+")) {
			return Integer.valueOf(s.split("\\.")[0]);
		}
		return pValue;
	}

	/**
	 * 是否是数字类型
	 * 
	 * @param pDbType
	 * @return
	 */
	public static boolean isNumberDbType(int pDbType) {
		if ((pDbType == -5) || (pDbType == 3) || (pDbType == 8)
				|| (pDbType == 6) || (pDbType == 4) || (pDbType == 2)
				|| (pDbType == 7) || (pDbType == 5) || (pDbType == -6)) {
			return true;
		}
		return false;
	}

	/**
	 * 将数字转换成字符串
	 * 
	 * @param number
	 * @param precision
	 * @return
	 */
	public static String parseNumber(Number number, int precision) {
		NumberFormat nbf = NumberFormat.getInstance();
		nbf.setMinimumFractionDigits(precision);
		nbf.setMaximumFractionDigits(precision);
		nbf.setGroupingUsed(false);

		return String.valueOf(Double.parseDouble(nbf.format(number)));
	}

}
