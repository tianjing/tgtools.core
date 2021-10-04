package tgtools.data;

import tgtools.exceptions.APPRuntimeException;
import tgtools.util.ReflectionUtil;
import tgtools.util.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.Date;

/**
 * @author tianjing
 */
public class DbTypeConverter {
    public static Object convertIntegerValue(Object pValue) {
        try {
            Long longValue = Long.valueOf(pValue.toString().trim());
            if (longValue.longValue() > 2147483647L) {
                return longValue;
            }
            return Integer.valueOf(pValue.toString().trim());
        } catch (Exception ex) {
        }
        return pValue;
    }

    public static Object toCommonType(Object pValue, int pCommonType, boolean pBolbUseStream) {
        Object value = pValue;
        if (value == null) {
            value = new DbNull();
        } else if (pCommonType == 4) {
            if ((pValue instanceof Integer)) {
                value = Integer.valueOf(pValue.toString());
            } else if ((value instanceof Double)) {
                int v = (int) Math.round(((Double) value).doubleValue());
                value = new Integer(v);
            } else {
                value = convertIntegerValue(pValue);
            }
        } else if ((value instanceof Number)) {
            if ((value instanceof BigDecimal)) {
                value = new BigDecimal(value.toString());
            } else if ((!(value instanceof Integer))
                    && (!(value instanceof Long))) {
                value = Double.valueOf(pValue.toString());
            }
        } else if ((value instanceof Blob)) {
            Blob blob = (Blob) value;
            try {
                if (pBolbUseStream) {
                    value = blob.getBinaryStream();
                } else {
                    value = blob.getBytes(1L, (int) blob.length());
                }
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
        }

        if ((value instanceof Number)) {
            if (value.toString().equals(Integer.valueOf(-2147483648))) {
                value = new DbNull();
            }

        }

        if (((value instanceof Date))
                && (StringUtil.equals(value.toString(), "0001-01-01 00:00:00.000000"))) {
            value = new DbNull();
        }

        return value;
    }

    public static Object toCommonType(Object pValue, int pCommonType) {
        return toCommonType(pValue, pCommonType, false);
    }

    public static Object toCommonType(String pValue, int pDbType) {
        if (pDbType == 2004) {
            return new byte[0];
        }

        Class<?> c = String.class;
        if ((pDbType == Types.NUMERIC) || (pDbType == Types.FLOAT)
                || (pDbType == Types.DOUBLE)) {
            c = Double.class;
        }
        if (pDbType == Types.INTEGER) {
            c = Integer.class;
        }
        if (pDbType == Types.BIGINT) {
            c = BigInteger.class;
        }
        if (pDbType == Types.DATE) {
            c = Timestamp.class;
        }
        if (pDbType == Types.TIME) {
            c = Time.class;
        }
        if (pDbType == Types.TIMESTAMP) {
            c = Timestamp.class;
        }
        return ReflectionUtil.instanceSimpleClass(c, pValue);
    }

    public static Object validateDbNullToNull(Object pValue) {
        if ((pValue == null) || ((pValue instanceof DbNull))) {
            return null;
        }
        return pValue;
    }

    public static Object validateNullToDbNull(Object pValue) {
        if (pValue == null) {
            return new DbNull();
        }
        return pValue;
    }

    public static String getDbTypeName(int pDbType) {
        switch (pDbType) {
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
            default:
                throw new DataAccessException(String.format("无法映射的数据库数据类型[%1$s]",
                        new Object[]{Integer.valueOf(pDbType)}));
        }
    }

    public static int getDbType(String pDbTypeName) {
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"CHAR")) {
            return Types.CHAR;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"INT32")) {
            return Types.INTEGER;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"VARCHAR")) {
            return Types.VARCHAR;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"INTEGER")) {
            return Types.INTEGER;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"NUMERIC")) {
            return Types.NUMERIC;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"DATE")) {
            return Types.DATE;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"TIME")) {
            return Types.TIME;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"TIMESTAMP")) {
            return Types.TIMESTAMP;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"DATETIME")) {
            return Types.TIMESTAMP;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"BLOB")) {
            return Types.BLOB;
        }
        if (StringUtil.equalsIgnoreCase(pDbTypeName,"CLOB")) {
            return Types.CLOB;
        }
        throw new DataAccessException(String.format("无法映射的中立数据类型[%1$s]",
                new Object[]{pDbTypeName}));
    }
}
