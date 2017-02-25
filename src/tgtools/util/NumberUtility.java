package tgtools.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

import tgtools.exceptions.APPRuntimeException;

public class NumberUtility {
	/**
	 * 将Number转换为BigDecimal
	 * 
	 * @param p_number
	 * @return
	 */
	public static BigDecimal toBigDecimal(Number p_number) {
		if ((p_number instanceof BigDecimal)) {
			return (BigDecimal) p_number;
		}
		if ((p_number instanceof Integer)) {
			return new BigDecimal(((Integer) p_number).intValue());
		}
		if ((p_number instanceof Double)) {
			return BigDecimal.valueOf(((Double) p_number).doubleValue());
		}
		if ((p_number instanceof Float)) {
			return BigDecimal.valueOf(((Float) p_number).doubleValue());
		}
		throw new APPRuntimeException(String.format(
				"不支持将数据类型[%1$s]转换为BigDecimal类型。", new Object[] { p_number
						.getClass().getName() }));
	}

	/**
	 * 将Number转换为Integer （去掉小数点）
	 * 
	 * @param p_value
	 * @return
	 */
	public static Number toInteger(Number p_value) {
		String s = p_value.toString();
		if (s.matches("(\\d+)\\.0+")) {
			return Integer.valueOf(s.split("\\.")[0]);
		}
		return p_value;
	}

	/**
	 * 是否是数字类型
	 * 
	 * @param p_dbType
	 * @return
	 */
	public static boolean isNumberDbType(int p_dbType) {
		if ((p_dbType == -5) || (p_dbType == 3) || (p_dbType == 8)
				|| (p_dbType == 6) || (p_dbType == 4) || (p_dbType == 2)
				|| (p_dbType == 7) || (p_dbType == 5) || (p_dbType == -6)) {
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
