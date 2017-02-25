package tgtools.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import tgtools.exceptions.APPRuntimeException;
import tgtools.util.ReflectionUtil;

public class DbTypeConverter {
	public static Object convertIntegerValue(Object p_value) {
		try {
			Long longValue = Long.valueOf(p_value.toString().trim());
			if (longValue.longValue() > 2147483647L) {
				return longValue;
			}
			return Integer.valueOf(p_value.toString().trim());
		} catch (Exception ex) {
		}
		return p_value;
	}

	public static Object toCommonType(Object p_value, int p_commonType) {
		Object value = p_value;
		if (value == null) {
			value = new DbNull();
		} else if (p_commonType == 4) {
			if ((p_value instanceof Integer)) {
				value = Integer.valueOf(p_value.toString());
			} else if ((value instanceof BigDecimal)) {
//				BigDecimal bg = (BigDecimal) value;
//
//				if (bg.scale() <= 0) {
//					value = convertIntegerValue(bg.toPlainString());
//				} else {
//					int v = (int) Math.round(Double.parseDouble(bg
//							.toPlainString()));
//
//					value = new Integer(v);
				//}
			} else if ((value instanceof Double)) {
				int v = (int) Math.round(((Double) value).doubleValue());
				value = new Integer(v);
			} else {
				value = convertIntegerValue(p_value);
			}
		} else if ((value instanceof Number)) {
			if ((value instanceof BigDecimal)) {
				//BigDecimal bg = (BigDecimal) value;
			//if (bg.scale() <= 0) {
			//		value = convertIntegerValue(bg.toPlainString());
				//}
			}

			if ((value instanceof BigDecimal)) {
				value = new BigDecimal(value.toString());
			} else if ((!(value instanceof Integer))
					&& (!(value instanceof Long))) {
				value = Double.valueOf(p_value.toString());
			}
		} else if ((value instanceof Blob)) {
			Blob blob = (Blob) value;
			try {
				value = blob.getBytes(1L, (int) blob.length());
			} catch (Exception e) {
				throw new APPRuntimeException("将Blob字段值以byte[]格式读出时发生异常。", e);
			}
		} else if ((value instanceof Clob)) {
			Clob clob = (Clob) value;
			try {
				value = clob.getSubString(1L, (int) clob.length());
			} catch (Exception e) {
				throw new APPRuntimeException("将Clob字段值以String格式读出时发生异常。", e);
			}
		} else {
			// throw new APPRuntimeException("不支持将Clob字段值转换为：" + p_commonType);
			// value = DbmsSpecificImpl.toCommonType(value, p_commonType);
		}

		if ((value instanceof Number)) {
			/*
			 * if (!(value instanceof Integer)) { value =
			 * NumberUtility.toInteger((Number) value); }
			 */
			if (value.toString().equals(Integer.valueOf(-2147483648))) {
				value = new DbNull();
			}

		}

		if (((value instanceof Date))
				&& (value.toString().equals("0001-01-01 00:00:00.000000"))) {
			value = new DbNull();
		}

		return value;
	}

	public static Object toCommonType(String p_value, int p_dbType) {
		if (p_dbType == 2004) {
			// if (p_value != null) {
			// return Base64.decodeBase64(p_value.getBytes());
			// }
			return new byte[0];
		}

		Class<?> c = String.class;
		if ((p_dbType == Types.NUMERIC) || (p_dbType == Types.FLOAT)
				|| (p_dbType == Types.DOUBLE)) {
			c = Double.class;
		}
		if (p_dbType == Types.INTEGER)
			c = Integer.class;
		if (p_dbType == Types.BIGINT)
			c = BigInteger.class;
		if (p_dbType == Types.DATE)
			c = Timestamp.class;
		if (p_dbType == Types.TIME)
			c = Time.class;
		if (p_dbType == Types.TIMESTAMP)
			c = Timestamp.class;
		return ReflectionUtil.instanceSimpleClass(c, p_value);
	}

	public static Object validateDbNullToNull(Object p_value) {
		if ((p_value == null) || ((p_value instanceof DbNull)))
			return null;
		return p_value;
	}
	public static Object validateNullToDbNull(Object p_value) {
		if (p_value == null) 
			return new DbNull();
		return p_value;
	}
	public static String getDbTypeName(int p_dbType) {
		switch (p_dbType) {
		case Types.CHAR:
			return "CHAR";
		case Types.VARCHAR:
			return "VARCHAR";
		case Types.INTEGER:
			return "INTEGER";
		case Types.BIGINT:
			return "NUMERIC";
		case Types.NUMERIC:
			return "NUMERIC";
		case Types.DECIMAL:
			return "NUMERIC";
		case Types.FLOAT:
			return "NUMERIC";
		case Types.DOUBLE:
			return "NUMERIC";
		case Types.DATE:
			return "DATE";
		case Types.TIME:
			return "TIME";
		case Types.TIMESTAMP:
			return "TIMESTAMP";
		case Types.BLOB:
			return "BLOB";
		case Types.LONGVARBINARY:
			return "BLOB";
		case Types.VARBINARY:
			return "BLOB";
		case Types.CLOB:
			return "CLOB";
		case Types.LONGVARCHAR:
			return "CLOB";
		case Types.REAL:
			return "NUMERIC";
		case Types.BOOLEAN:
			return "VARCHAR";
		}
		throw new DataAccessException(String.format("无法映射的数据库数据类型[%1$s]",
				new Object[] { Integer.valueOf(p_dbType) }));
	}

	public static int getDbType(String p_dbTypeName) {
		if (p_dbTypeName.equalsIgnoreCase("CHAR"))
			return Types.CHAR;
		if (p_dbTypeName.equalsIgnoreCase("INT32"))
			return Types.INTEGER;
		if (p_dbTypeName.equalsIgnoreCase("VARCHAR"))
			return Types.VARCHAR;
		if (p_dbTypeName.equalsIgnoreCase("INTEGER"))
			return Types.INTEGER;
		if (p_dbTypeName.equalsIgnoreCase("NUMERIC"))
			return Types.NUMERIC;
		if (p_dbTypeName.equalsIgnoreCase("DATE"))
			return Types.DATE;
		if (p_dbTypeName.equalsIgnoreCase("TIME"))
			return Types.TIME;
		if (p_dbTypeName.equalsIgnoreCase("TIMESTAMP"))
			return Types.TIMESTAMP;
		if (p_dbTypeName.equalsIgnoreCase("DATETIME"))
			return Types.TIMESTAMP;
		if (p_dbTypeName.equalsIgnoreCase("BLOB"))
			return Types.BLOB;
		if (p_dbTypeName.equalsIgnoreCase("CLOB")) {
			return Types.CLOB;
		}
		throw new DataAccessException(String.format("无法映射的中立数据类型[%1$s]",
				new Object[] { p_dbTypeName }));
	}
}
