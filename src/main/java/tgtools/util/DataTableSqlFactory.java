package tgtools.util;

import tgtools.data.*;
import tgtools.log.LoggerFactory;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 将DataTable转换成sql的类
 *
 * @author TianJing
 */
public class DataTableSqlFactory {
    private static String m_SeparateStr = ",";
    private static String m_NullValue = "null";

    /**
     * 将DataTable 转换成Insert SQL集合
     *
     * @param pTable     数据Table
     * @param pTableName 表名
     * @return
     */
    public static List<String> buildInsertSql(DataTable pTable,
                                              String pTableName) {
        List<String> sqls = new ArrayList<String>();
        for (int i = 0; i < pTable.getRows().size(); i++) {
            String sql = buildInsertSql(pTable.getRow(i), pTableName);
            if (!StringUtil.isNullOrEmpty(sql)) {
                sqls.add(sql);
            }
        }
        return sqls;
    }

    /**
     * 将一行数据转换成 Insert SQL
     *
     * @param pRow       数据行
     * @param pTableName 表名称
     * @return
     */
    public static String buildInsertSql(DataRow pRow, String pTableName) {
        String sql = "Insert into {table} ({column}) values({values})";

        String columns = getColumns(pRow.getTable().getColumns());
        String values = getValues(pRow);

        sql = sql.replace("{table}", pTableName);
        sql = sql.replace("{column}", columns);
        sql = sql.replace("{values}", values);
        return sql + ";";
    }

    /**
     * 将DataTable 转换成Update SQL集合
     *
     * @param pTable     数据table
     * @param pTableName 表名称
     * @return
     */
    public static List<String> buildUpdateSql(DataTable pTable,
                                              String pTableName) {
        List<String> sqls = new ArrayList<String>();
        for (int i = 0; i < pTable.getRows().size(); i++) {
            String sql = buildUpdateSql(pTable.getRow(i), pTableName);
            if (!StringUtil.isNullOrEmpty(sql)) {
                sqls.add(sql);
            }
        }
        return sqls;

    }

    /**
     * 将一行数据转换成 Update SQL
     *
     * @param pRow       数据行
     * @param pTableName 表名称
     * @return
     */
    public static String buildUpdateSql(DataRow pRow, String pTableName) {
        String sql = "Update {table} set {values} where {filter}";

        String values = getUpdateValues(pRow);
        String filter = getFilter(pRow);
        sql = sql.replace("{table}", pTableName);
        sql = sql.replace("{filter}", filter);
        sql = sql.replace("{values}", values);
        return sql + ";";

    }

    /**
     * 将DataTable 转换成Delete SQL集合
     *
     * @param pTable
     * @param pTableName
     * @return
     */
    public static List<String> buildDeleteSql(DataTable pTable,
                                              String pTableName) {
        List<String> sqls = new ArrayList<String>();
        for (int i = 0; i < pTable.getRows().size(); i++) {
            String sql = buildDeleteSql(pTable.getRow(i), pTableName);
            if (!StringUtil.isNullOrEmpty(sql)) {
                sqls.add(sql);
            }
        }
        return sqls;
    }

    /**
     * 将一行数据转换成 Delete SQL
     *
     * @param pRow
     * @param pTableName
     * @return
     */
    public static String buildDeleteSql(DataRow pRow, String pTableName) {
        String sql = "Delete {table} where {filter}";
        String filter = getFilter(pRow);
        if (!StringUtil.isNullOrEmpty(filter)) {
            sql = sql.replace("{table}", pTableName);
            sql = sql.replace("{filter}", filter);

            return sql + ";";
        }
        return StringUtil.EMPTY_STRING;

    }

    /**
     * 将行数据转换成 赋值语句 （相当于 set 后面的内容）
     *
     * @param pRow 数据行
     * @return
     */
    private static String getUpdateValues(DataRow pRow) {
        StringBuilder sb = new StringBuilder();
        Collection<DataColumn> columns = pRow.getTable().getColumns().values();
        for (DataColumn item : columns) {
            if (item.isPrimaryKey()) {
                continue;
            }
            String name = item.getColumnName();
            Object value = "";

            value = vaildValue(pRow.getValue(name), item.getColumnType());
            sb.append(name + "=" + value + m_SeparateStr);
        }
        sb = RemoveSeparateStr(sb);
        return sb.toString();
    }

    /**
     * 将行数据转换成 条件语句 （相当于 where 后面的内容）
     *
     * @param pRow 数据行
     * @return
     */
    private static String getFilter(DataRow pRow) {
        StringBuilder sb = new StringBuilder();
        Collection<DataColumn> columns = pRow.getTable().getColumns().values();
        for (DataColumn item : columns) {
            if (item.isPrimaryKey()) {
                String name = item.getColumnName();
                String value = vaildValue(pRow.getValue(name),
                        item.getColumnType());
                sb.append(" and " + name + "=" + value);
            }
        }

        if (sb.length() > 0) {
            sb.insert(0, " 1=1");
        }

        return sb.toString();
    }

    /**
     * 将数据列信息转换成 Insert 用的值（相当于values 前面的内容）
     *
     * @param pColumns
     * @return
     */
    private static String getColumns(DataColumnCollection pColumns) {
        StringBuilder sb = new StringBuilder();
        for (DataColumn key : pColumns.values()) {
            sb.append(key.getColumnName() + m_SeparateStr);
        }

        sb = RemoveSeparateStr(sb);
        return sb.toString();
    }

    /**
     * 将数据行转换成 Insert 用的值 （相当于 values 后的内容）
     *
     * @param pRow 数据行
     * @return
     */
    private static String getValues(DataRow pRow) {
        if (null == pRow) {
            return "";
        }
        Collection<DataColumn> columns = pRow.getTable().getColumns().values();
        StringBuilder sb = new StringBuilder();
        for (DataColumn item : columns) {
            sb.append(vaildValue(pRow.getValue(item.getColumnName()),
                    item.getColumnType())
                    + m_SeparateStr);
        }
        sb = RemoveSeparateStr(sb);
        return sb.toString();
    }

    /**
     * 将数据OBJECT 转换成 有效的sql 值
     *
     * @param pValue    数据
     * @param pDataType 数据类型 java.sql.Types
     * @return
     */
    @SuppressWarnings("deprecation")
    private static String vaildValue(Object pValue, int pDataType) {
        if (null == pValue || pValue instanceof DbNull) {
            return "null";
        }
        String value = pValue.toString();
        value = value.replace("\"", "");
        if (java.sql.Types.VARCHAR == pDataType
                || java.sql.Types.CHAR == pDataType
                || java.sql.Types.CLOB == pDataType
        ) {
            return "'" + SqlStrHelper.escape(pValue.toString()) + "'";
        } else if (java.sql.Types.INTEGER == pDataType
                || java.sql.Types.BIGINT == pDataType
                || java.sql.Types.NUMERIC == pDataType
                || java.sql.Types.DECIMAL == pDataType
                || java.sql.Types.DOUBLE == pDataType
                || java.sql.Types.REAL == pDataType
                || java.sql.Types.FLOAT == pDataType) {

            if (value.isEmpty()) {
                return m_NullValue;
            } else {
                return value;
            }
        } else if (java.sql.Types.TIMESTAMP == pDataType
                || java.sql.Types.TIME == pDataType
                || java.sql.Types.DATE == pDataType) {
            if (value.isEmpty()) {
                return m_NullValue;
            } else {
                try {
                    if (pValue instanceof java.util.Date) {
                        return "'" + tgtools.util.DateUtil.formatFullLongtime((java.util.Date) pValue)
                                + "'";
                    } else {
                        if (value.indexOf("-") >= 0) {
                            value = StringUtil.replace(value, "-", "/");
                        }
                        Date date = new Date(Date.parse(value));

                        return "'" + tgtools.util.DateUtil.formatFullLongtime(date)
                                + "'";
                    }
                } catch (Exception e) {
                    LoggerFactory.getDefault().error("日期转换错误：" + pValue, e);
                    return m_NullValue;
                }
            }

        } else if (Types.BLOB == pDataType && pValue instanceof byte[]) {
            return "0x" + ConverHelper.toHexString((byte[]) pValue);
        } else if (java.sql.Types.NULL == pDataType) {
            return value;
        } else {
            return m_NullValue;

        }
    }

    /**
     * 如果最后一个字符是分隔符则删除，如果不是则原样输出
     *
     * @param pStr
     * @return
     */
    private static StringBuilder RemoveSeparateStr(StringBuilder pStr) {
        if (pStr.length() > 0
                && m_SeparateStr.equals(pStr.substring(pStr.length() - 1))) {
            pStr.deleteCharAt(pStr.length() - 1);
        }
        return pStr;
    }




}
